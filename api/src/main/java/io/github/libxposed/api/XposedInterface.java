package io.github.libxposed.api;

import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Executor;

import io.github.libxposed.api.error.HookFailedError;
import io.github.libxposed.annotation.SinceApi;

/**
 * Xposed interface for modules to operate on application processes.
 */
@SuppressWarnings("unused")
public interface XposedInterface {
    /**
     * API version 101.
     * <p>Behavior changes: all modules</p>
     * <ul>
     * <li>Modules cannot be injected into zygote;
     * they are only loaded within the process of the scope.</li>
     * </ul>
     * <p>Behavior changes: Modules targeting 101 or higher</p>
     * <ul>
     * <li>This is the first API version.</li>
     * </ul>
     */
    int API_101 = 101;

    /**
     * API version 102.
     * <p>New features</p>
     * <ul>
     * <li>Hot reload allows modules to be updated without restarting the process.</li>
     * <li>Module entries can stop receiving subsequent lifecycle callbacks.</li>
     * <li>Hooks can be atomically replaced by api or same id.</li>
     * </ul>
     * <p>Behavior changes: Modules targeting 102 or higher</p>
     * <ul>
     * <li>Libxposed modules can not call legacy {@code de.robv.android.xposed} APIs.</li>
     * </ul>
     */
    int API_102 = 102;

    /**
     * API version 103.
     * <p>New features</p>
     * <ul>
     * <li>Modules can bind to a module application service shared by hooked processes.</li>
     * </ul>
     */
    int API_103 = 103;

    /**
     * The API version of this <b>library</b>. This is a static value for the framework.
     * Modules should use {@link #getApiVersion()} to check the API version at runtime.
     */
    int LIB_API = API_103;

    /**
     * The framework has the capability to hook system_server and other system processes.
     */
    long PROP_CAP_SYSTEM = 1L;
    /**
     * The framework provides remote preferences and remote files support.
     */
    long PROP_CAP_REMOTE = 1L << 1;
    /**
     * The framework disallows accessing Xposed API via reflection or dynamically loaded code.
     */
    long PROP_RT_API_PROTECTION = 1L << 2;
    /**
     * The default hook priority.
     */
    int PRIORITY_DEFAULT = 50;
    /**
     * Execute at the end of the interception chain.
     */
    int PRIORITY_LOWEST = Integer.MIN_VALUE;
    /**
     * Execute at the beginning of the interception chain.
     */
    int PRIORITY_HIGHEST = Integer.MAX_VALUE;

    /**
     * Invoker for a method or constructor. Invocations through invokers will bypass access checks.
     */
    interface Invoker<T extends Invoker<T, U>, U extends Executable> {
        /**
         * Type of the invoker, which determines the hook chain to be invoked
         */
        sealed interface Type permits Type.Origin, Type.Chain {
            /**
             * A convenience constant for {@link Origin}.
             */
            Origin ORIGIN = new Origin();

            /**
             * Invokes the original executable, skipping all hooks.
             */
            record Origin() implements Type {
            }

            /**
             * Invokes the executable starting from the middle of the hook chain, skipping all
             * hooks with priority higher than the given value.
             *
             * @param maxPriority The maximum priority of hooks to include in the chain
             */
            record Chain(int maxPriority) implements Type {
                /**
                 * Invoking the executable with full hook chain.
                 */
                public static final Chain FULL = new Chain(PRIORITY_HIGHEST);
            }
        }

        /**
         * Sets the type of the invoker, which determines the hook chain to be invoked
         */
        T setType(@NonNull Type type);

        /**
         * Invokes the method (or the constructor as a method) through the hook chain determined by
         * the invoker's type.
         *
         * @param thisObject For non-static calls, the {@code this} pointer, otherwise {@code null}
         * @param args       The arguments used for the method call
         * @return The result returned from the invoked method
         * <p>For void methods and constructors, always returns {@code null}.</p>
         * @see Method#invoke(Object, Object...)
         */
        Object invoke(Object thisObject, Object... args) throws InvocationTargetException, IllegalArgumentException, IllegalAccessException;

        /**
         * Invokes the special (non-virtual) method (or the constructor as a method) on a given object
         * instance, similar to the functionality of {@code CallNonVirtual<type>Method} in JNI, which invokes
         * an instance (nonstatic) method on a Java object. This method is useful when you need to call
         * a specific method on an object, bypassing any overridden methods in subclasses and
         * directly invoking the method defined in the specified class.
         *
         * <p>This method is useful when you need to call {@code super.xxx()} in a hooked constructor.</p>
         *
         * @param thisObject The {@code this} pointer
         * @param args       The arguments used for the method call
         * @return The result returned from the invoked method
         * <p>For void methods and constructors, always returns {@code null}.</p>
         * @see Method#invoke(Object, Object...)
         */
        Object invokeSpecial(@NonNull Object thisObject, Object... args) throws InvocationTargetException, IllegalArgumentException, IllegalAccessException;
    }

    /**
     * Invoker for a constructor.
     *
     * @param <T> The type of the constructor
     */
    interface CtorInvoker<T> extends Invoker<CtorInvoker<T>, Constructor<T>> {
        /**
         * Creates a new instance through the hook chain determined by the invoker's type.
         *
         * @param args The arguments used for the construction
         * @return The instance created and initialized by the constructor
         * @see Constructor#newInstance(Object...)
         */
        @NonNull
        T newInstance(Object... args) throws InvocationTargetException, IllegalArgumentException, IllegalAccessException, InstantiationException;

        /**
         * Creates a new instance of the given subclass, but initializes it with a parent constructor. This could
         * leave the object in an invalid state, where the subclass constructor is not called and the fields
         * of the subclass are not initialized.
         *
         * <p>This method is useful when you need to initialize some fields in the subclass by yourself.</p>
         *
         * @param <U>      The type of the subclass
         * @param subClass The subclass to create a new instance
         * @param args     The arguments used for the construction
         * @return The instance of subclass initialized by the constructor
         * @see Constructor#newInstance(Object...)
         */
        @NonNull
        <U> U newInstanceSpecial(@NonNull Class<U> subClass, Object... args) throws InvocationTargetException, IllegalArgumentException, IllegalAccessException, InstantiationException;
    }

    /**
     * Interceptor chain for a method or constructor. Chain objects cannot be shared among threads or
     * reused after {@link Hooker#intercept(Chain)} ends.
     */
    interface Chain {
        /**
         * Gets the method / constructor being hooked.
         */
        @NonNull
        Executable getExecutable();

        /**
         * Gets the {@code this} pointer for the call, or {@code null} for static methods.
         */
        Object getThisObject();

        /**
         * Gets the arguments. The returned list is immutable. If you want to change the arguments, you
         * should call {@code proceed(Object...)} or {@code proceedWith(Object, Object...)} with the new
         * arguments.
         */
        @NonNull
        List<Object> getArgs();

        /**
         * Gets the argument at the given index.
         *
         * @param index The argument index
         * @return The argument at the given index
         * @throws IndexOutOfBoundsException if index is out of bounds
         * @throws ClassCastException        if the argument cannot be cast to the expected type
         */
        Object getArg(int index) throws IndexOutOfBoundsException, ClassCastException;

        /**
         * Proceeds to the next interceptor in the chain with the same arguments and {@code this} pointer.
         *
         * @return The result returned from next interceptor or the original executable if current
         * interceptor is the last one in the chain.
         * <p>For void methods and constructors, always returns {@code null}.</p>
         * @throws Throwable if any interceptor or the original executable throws an exception
         */
        Object proceed() throws Throwable;

        /**
         * Proceeds to the next interceptor in the chain with the given arguments and the same {@code this} pointer.
         *
         * @param args The arguments used for the call
         * @return The result returned from next interceptor or the original executable if current
         * interceptor is the last one in the chain.
         * <p>For void methods and constructors, always returns {@code null}.</p>
         * @throws Throwable if any interceptor or the original executable throws an exception
         */
        Object proceed(@NonNull Object[] args) throws Throwable;

        /**
         * Proceeds to the next interceptor in this chain with the same arguments and the given
         * {@code this} object. Static method interceptors should not call this method.
         *
         * <p>This changes only the receiver used by the rest of the current chain. It does not change
         * the executable returned by {@link #getExecutable()}, select a different override, or switch
         * to another executable's hook chain. When this chain reaches its end, the framework invokes
         * the original implementation of {@link #getExecutable()} on {@code thisObject}.</p>
         *
         * <p>For example, if {@link #getExecutable()} is {@code Parent.foo}, calling this method with
         * a {@code Child} object still invokes the original {@code Parent.foo} on that object. It does
         * not dispatch to {@code Child.foo} or run hooks registered for {@code Child.foo}. Use an
         * {@link Invoker} to start a new invocation when virtual dispatch is desired.</p>
         *
         * <p>{@code thisObject} must be an instance of the declaring class of
         * {@link #getExecutable()}.</p>
         *
         * @param thisObject The {@code this} object for the call
         * @return The result returned from the next interceptor, or from the original executable if
         * the current interceptor is the last one in the chain.
         * <p>For void methods and constructors, always returns {@code null}.</p>
         * @throws IllegalArgumentException if {@code thisObject} is not compatible with the declaring
         *                                  class of {@link #getExecutable()}
         * @throws Throwable if any interceptor or the original executable throws an exception
         */
        Object proceedWith(@NonNull Object thisObject) throws Throwable;

        /**
         * Proceeds to the next interceptor in this chain with the given arguments and {@code this}
         * object. Static method interceptors should not call this method.
         *
         * <p>This changes the receiver and arguments used by the rest of the current chain, but not
         * which executable the chain belongs to. It does not select a different override or switch to
         * another executable's hook chain. When this chain reaches its end, the framework invokes the
         * original implementation of {@link #getExecutable()} on {@code thisObject}, using
         * {@code args}.</p>
         *
         * <p>For example, if {@link #getExecutable()} is {@code Parent.foo}, calling this method with
         * a {@code Child} object still invokes the original {@code Parent.foo} on that object. It does
         * not dispatch to {@code Child.foo} or run hooks registered for {@code Child.foo}. Use an
         * {@link Invoker} to start a new invocation when virtual dispatch is desired.</p>
         *
         * <p>{@code thisObject} must be an instance of the declaring class of
         * {@link #getExecutable()}.</p>
         *
         * @param thisObject The {@code this} object for the call
         * @param args       The arguments used for the call
         * @return The result returned from the next interceptor, or from the original executable if
         * the current interceptor is the last one in the chain.
         * <p>For void methods and constructors, always returns {@code null}.</p>
         * @throws IllegalArgumentException if {@code thisObject} is not compatible with the declaring
         *                                  class of {@link #getExecutable()}
         * @throws Throwable if any interceptor or the original executable throws an exception
         */
        Object proceedWith(@NonNull Object thisObject, @NonNull Object[] args) throws Throwable;
    }

    /**
     * Hooker for a method or constructor.
     */
    interface Hooker {
        /**
         * Intercepts a method / constructor call.
         *
         * @param chain The interceptor chain for the call
         * @return The result to be returned from the interceptor. If the hooker does not want to
         * change the result, it should call {@code chain.proceed()} and return its result.
         * <p>For void methods and constructors, the return value is ignored by the framework.</p>
         * @throws Throwable Throw any exception from the interceptor. The exception will
         *                   propagate to the caller if not caught by any interceptor.
         */
        Object intercept(@NonNull Chain chain) throws Throwable;
    }

    /**
     * Handle for a hook.
     */
    interface HookHandle {
        /**
         * Gets the method / constructor being hooked.
         */
        @NonNull
        Executable getExecutable();

        /**
         * Cancels the hook. This method is idempotent. It is safe to call this method multiple times.
         */
        void unhook();

        /**
         * Gets the unique id of the hook, or null if the hook is not assigned with an id.
         */
        @SinceApi(API_102)
        @Nullable
        String getId();

        /**
         * Atomically replaces this hook with a new hooker and returns the new hook handle.
         * <p>
         * The replacement keeps the executable, priority, exception handling mode, and id of this hook.
         * For a hook with an id, this targets the same hook as creating a new hook on the same executable
         * with the same id. This method is the handle-based form of replacement and can also replace a
         * hook without an id. It is useful during hot reloading when new code receives old hook handles
         * from {@link XposedModuleInterface.HotReloadedParam#getOldHookHandles()}. After a successful
         * replacement, this handle is no longer valid.
         * </p>
         * <p>The hook chain is snapshot based. Replacing a hook while a call is running does not affect
         * that in-flight call.</p>
         *
         * @param hooker The new hooker object
         * @return The new handle for the replaced hook
         * @throws IllegalArgumentException if hooker is invalid
         * @throws IllegalStateException    if this hook handle is no longer valid
         * @throws HookFailedError          if replacement fails due to framework internal error
         */
        @SinceApi(API_102)
        @NonNull
        HookHandle replaceHook(@NonNull Hooker hooker);
    }

    /**
     * Receives changes to a module application's remote service connection.
     */
    @SinceApi(API_103)
    interface RemoteServiceConnection {
        /**
         * Called when the service is connected. This can be called again with a new Binder after
         * {@link #onServiceDisconnected()}.
         *
         * @param service Binder returned by the module application's Android service
         */
        void onServiceConnected(@NonNull IBinder service);

        /**
         * Called when a previously connected service dies. The framework keeps trying to maintain
         * the binding while the returned handle remains open.
         */
        default void onServiceDisconnected() {
        }

        /**
         * Called when the service cannot be bound. This is a terminal failure for this handle.
         *
         * @param message Framework-provided diagnostic message
         */
        default void onServiceConnectionFailed(@NonNull String message) {
        }
    }

    /**
     * Handle retaining a module application's remote service binding for this process.
     */
    @SinceApi(API_103)
    interface RemoteServiceHandle extends AutoCloseable {
        /**
         * Releases this process's interest in the service. This method is idempotent.
         */
        @Override
        void close();
    }

    /**
     * Exception handling mode for hookers. This determines how the framework handles exceptions
     * thrown by hookers. The default mode is {@link ExceptionMode#DEFAULT}.
     */
    enum ExceptionMode {
        /**
         * Follows the global exception mode configured in {@code module.prop}. Defaults to {@link #PROTECTIVE}
         * if not specified.
         */
        DEFAULT,

        /**
         * Any exception thrown by the <b>hooker</b> will be caught and logged, and the call will proceed as
         * if no hook exists. This mode is recommended for most cases, as it can prevent crashes caused by
         * hook errors.
         * <p>
         * If the exception is thrown before {@link Chain#proceed()}, the framework will
         * continue the chain without the hook; if the exception is thrown after proceed, the framework
         * will return the value / exception proceeded as the result.
         * </p>
         * <p>Exceptions thrown by proceed will always be propagated.</p>
         */
        PROTECTIVE,

        /**
         * Any exception thrown by the hooker will be propagated to the caller as usual. This mode is
         * recommended for debugging purposes, as it can help you find and fix errors in your hooks.
         */
        PASSTHROUGH,
    }

    /**
     * Builder for configuring a hook.
     */
    interface HookBuilder {
        /**
         * Sets the priority of the hook. Hooks with higher priority will be called before hooks with lower
         * priority. The default priority is {@link XposedInterface#PRIORITY_DEFAULT}.
         *
         * @param priority The priority of the hook
         * @return The builder itself for chaining
         */
        HookBuilder setPriority(int priority);

        /**
         * Sets the exception handling mode for the hook. The default mode is {@link ExceptionMode#DEFAULT}.
         *
         * @param mode The exception handling mode
         * @return The builder itself for chaining
         */
        HookBuilder setExceptionMode(@NonNull ExceptionMode mode);

        /**
         * Sets the hooker for the method / constructor and builds the hook.
         *
         * @param hooker The hooker object
         * @return The handle for the hook
         * @throws IllegalArgumentException if origin is framework internal or {@link Constructor#newInstance},
         *                                  or hooker is invalid
         * @throws HookFailedError          if hook fails due to framework internal error
         */
        @NonNull
        HookHandle intercept(@NonNull Hooker hooker);

        /**
         * Sets a unique id for the hook, default to {@code null}. An id is used for exclusively identifying
         * a hook in the same module on the executable. A new hook with the same id in the same module on
         * the executable will replace the old one atomically, and the old hook handle will be invalid.
         * Hook ids are isolated between modules.
         *
         * <p>The hook chain is snapshot based. Replacing or adding a hook while a call is running does not
         * affect that in-flight call.</p>
         *
         * @param id The id for the hook. It can be null if you don't care about replacing the hook later.
         * @return The builder itself for chaining
         */
        @SinceApi(API_102)
        HookBuilder setId(@Nullable String id);
    }

    /**
     * Gets the runtime Xposed API version. Framework implementations <b>must not</b> override this method.
     */
    default int getApiVersion() {
        return LIB_API;
    }

    /**
     * Gets the Xposed framework name of current implementation.
     */
    @NonNull
    String getFrameworkName();

    /**
     * Gets the Xposed framework version of current implementation.
     */
    @NonNull
    String getFrameworkVersion();

    /**
     * Gets the Xposed framework version code of current implementation.
     */
    long getFrameworkVersionCode();

    /**
     * Gets the Xposed framework properties.
     * Properties with prefix {@code PROP_RT_} may change among launches.
     */
    long getFrameworkProperties();

    /**
     * Hook a method / constructor.
     *
     * @param origin The executable to be hooked
     * @return The builder for the hook
     */
    @NonNull
    HookBuilder hook(@NonNull Executable origin);

    /**
     * Hook the static initializer ({@code <clinit>}) of a class.
     *
     * <p>The static initializer is treated as a regular {@code static void()} method with no parameters.
     * Accordingly, in the {@link Chain} passed to the hooker:</p>
     * <ul>
     *     <li>{@link Chain#getExecutable()} returns a synthetic {@link Method} representing
     *     the static initializer.</li>
     *     <li>{@link Chain#getThisObject()} always returns {@code null}.</li>
     *     <li>{@link Chain#getArgs()} returns an empty list.</li>
     *     <li>{@link Chain#proceed()} returns {@code null}.</li>
     * </ul>
     *
     * <p>Note: If the class is already initialized, the hook will never be called.</p>
     *
     * @param origin The class whose static initializer is to be hooked
     * @return The builder for the hook
     */
    @NonNull
    HookBuilder hookClassInitializer(@NonNull Class<?> origin);

    /**
     * Deoptimizes a method / constructor in case hooked callee is not called because of inline.
     *
     * <p>By deoptimizing the method, the runtime will fall back to calling all callees without inlining.
     * For example, when a short hooked method B is invoked by method A, the callback to B is not invoked
     * after hooking, which may mean A has inlined B inside its method body. To force A to call the hooked B,
     * you can deoptimize A and then your hook can take effect.</p>
     *
     * <p>Generally, you need to find all the callers of your hooked callee, and that can hardly be achieved
     * (but you can still search all callers by using <a href="https://github.com/LuckyPray/DexKit">DexKit</a>).
     * Use this method if you are sure the deoptimized callers are all you need. Otherwise, it would be better to
     * change the hook point or to deoptimize the whole app manually (by simply reinstalling the app without uninstall).</p>
     *
     * @param executable The method / constructor to deoptimize
     * @return Indicate whether the deoptimizing succeed or not
     */
    boolean deoptimize(@NonNull Executable executable);

    /**
     * Get a method invoker for the given method. Invocations through invokers will bypass access
     * checks. The default type of the invoker is {@link Invoker.Type.Chain#FULL}.
     *
     * @param method The method to get the invoker for
     * @return The method invoker
     */
    @NonNull
    Invoker<?, Method> getInvoker(@NonNull Method method);

    /**
     * Get a constructor invoker for the given constructor. Invocations through invokers will bypass
     * access checks. The default type of the invoker is {@link Invoker.Type.Chain#FULL}.
     *
     * @param constructor The constructor to get the invoker for
     * @param <T>         The type of the constructor
     * @return The constructor invoker
     */
    @NonNull
    <T> CtorInvoker<T> getInvoker(@NonNull Constructor<T> constructor);

    /**
     * Writes a message to the Xposed log.
     *
     * @param priority The log priority, see {@link android.util.Log}
     * @param tag      The log tag
     * @param msg      The log message
     */
    void log(int priority, @Nullable String tag, @NonNull String msg);

    /**
     * Writes a message to the Xposed log.
     *
     * @param priority The log priority, see {@link android.util.Log}
     * @param tag      The log tag
     * @param msg      The log message
     * @param tr       An exception to log
     */
    void log(int priority, @Nullable String tag, @NonNull String msg, @Nullable Throwable tr);

    /**
     * Gets the application info of the module.
     */
    @NonNull
    ApplicationInfo getModuleApplicationInfo();

    /**
     * Gets remote preferences stored in Xposed framework. Note that those are read-only in hooked apps.
     *
     * @param group Group name
     * @return The preferences
     * @throws UnsupportedOperationException If the framework is embedded
     */
    @NonNull
    SharedPreferences getRemotePreferences(@NonNull String group);

    /**
     * List all files in the module's shared data directory.
     *
     * @return The file list
     * @throws UnsupportedOperationException If the framework is embedded
     */
    @NonNull
    String[] listRemoteFiles();

    /**
     * Open a file in the module's shared data directory. The file is opened in read-only mode.
     *
     * @param name File name, must not contain path separators and . or ..
     * @return The file descriptor
     * @throws FileNotFoundException         If the file does not exist or the path is forbidden
     * @throws UnsupportedOperationException If the framework is embedded
     */
    @NonNull
    ParcelFileDescriptor openRemoteFile(@NonNull String name) throws FileNotFoundException;

    /**
     * Binds the service declared by the module application. All hooked processes using the same
     * module and Android user share one Android service instance, but each call receives its own
     * connection callbacks. The framework keeps the Android service bound while at least one
     * hooked process retains a handle.
     * <p>
     * Callback methods are serialized in connection order and dispatched through {@code executor}.
     * Binder transactions made through the returned service can still throw
     * {@link android.os.DeadObjectException}; callers must not transparently retry non-idempotent
     * transactions.
     * </p>
     *
     * @param executor   Executor used for connection callbacks
     * @param connection Connection callback
     * @return Handle that releases this process's binding when closed
     * @throws UnsupportedOperationException If the framework or module does not support a remote
     *                                       service
     */
    @SinceApi(API_103)
    @NonNull
    RemoteServiceHandle bindRemoteService(@NonNull Executor executor,
                                          @NonNull RemoteServiceConnection connection);
}
