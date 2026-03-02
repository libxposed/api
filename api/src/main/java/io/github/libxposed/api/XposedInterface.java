package io.github.libxposed.api;

import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.os.ParcelFileDescriptor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import io.github.libxposed.api.errors.HookFailedError;

/**
 * Xposed interface for modules to operate on application processes.
 */
@SuppressWarnings("unused")
public interface XposedInterface {
    /**
     * The framework has the capability to hook system_server and other system processes.
     */
    long CAP_SYSTEM = 1L;
    /**
     * The framework provides remote preferences and remote files support.
     */
    long CAP_REMOTE = 1L << 1;
    /**
     * The framework allows dynamically loaded code to use Xposed APIs.
     */
    long CAP_RT_DYNAMIC_CODE_API_ACCESS = 1L << 2;

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
     * Invoker for a method or constructor.
     *
     * @param <T> {@link Method} or {@link Constructor}
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
    }

    /**
     * Invoker for a method.
     */
    interface MethodInvoker extends Invoker<MethodInvoker, Method> {
        /**
         * Invokes the method through the hook chain determined by the invoker's type.
         *
         * @param thisObject For non-static calls, the {@code this} pointer, otherwise {@code null}
         * @param args       The arguments used for the method call
         * @return The result returned from the invoked method
         * @see Method#invoke(Object, Object...)
         */
        @Nullable
        Object invoke(@Nullable Object thisObject, Object... args) throws InvocationTargetException, IllegalArgumentException, IllegalAccessException;

        /**
         * Invokes a special (non-virtual) method on a given object instance, similar to the functionality of
         * {@code CallNonVirtual<type>Method} in JNI, which invokes an instance (nonstatic) method on a Java
         * object. This method is useful when you need to call a specific method on an object, bypassing any
         * overridden methods in subclasses and directly invoking the method defined in the specified class.
         *
         * <p>This method is useful when you need to call {@code super.xxx()} in a hooked constructor.</p>
         *
         * @param thisObject The {@code this} pointer
         * @param args       The arguments used for the method call
         * @return The result returned from the invoked method
         * @see Method#invoke(Object, Object...)
         */
        @Nullable
        Object invokeSpecial(@NonNull Object thisObject, Object... args) throws InvocationTargetException, IllegalArgumentException, IllegalAccessException;
    }

    /**
     * Invoker for a constructor.
     *
     * @param <T> The type of the constructor
     */
    interface CtorInvoker<T> extends Invoker<CtorInvoker<T>, Constructor<T>> {
        /**
         * Invokes the constructor as a method on an existing instance through the hook chain
         * determined by the invoker's type.
         *
         * @param thisObject The instance to be constructed
         * @param args       The arguments used for the construction
         * @see Constructor#newInstance(Object...)
         */
        void invoke(@NonNull T thisObject, Object... args) throws InvocationTargetException, IllegalArgumentException, IllegalAccessException;

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
         * Invokes a special (non-virtual) method on a given object instance, similar to the functionality of
         * {@code CallNonVirtual<type>Method} in JNI, which invokes an instance (nonstatic) method on a Java
         * object. This method is useful when you need to call a specific method on an object, bypassing any
         * overridden methods in subclasses and directly invoking the method defined in the specified class.
         *
         * <p>This method is useful when you need to call {@code super.xxx()} in a hooked constructor.</p>
         *
         * @param thisObject The instance to be constructed
         * @param args       The arguments used for the construction
         * @see Constructor#newInstance(Object...)
         */
        void invokeSpecial(@NonNull T thisObject, Object... args) throws InvocationTargetException, IllegalArgumentException, IllegalAccessException;

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
     * Interceptor chain for a method or constructor.
     */
    interface Chain<T extends Executable> {
        /**
         * Gets the method / constructor being hooked.
         */
        @NonNull
        T getExecutable();

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
        @Nullable
        <U> U getArg(int index) throws IndexOutOfBoundsException, ClassCastException;
    }

    /**
     * Interceptor chain for a method.
     */
    interface MethodChain extends Chain<Method> {
        /**
         * Gets the {@code this} pointer for the method call, or {@code null} for static calls.
         */
        @Nullable
        Object getThisObject();

        /**
         * Proceeds to the next interceptor in the chain with the same arguments and {@code this} pointer.
         *
         * @return The result returned from next interceptor or the original method if current
         * interceptor is the last one in the chain. For void methods, always returns {@code null}.
         * @throws Throwable if any interceptor or the original method throws an exception
         */
        @Nullable
        Object proceed() throws Throwable;

        /**
         * Proceeds to the next interceptor in the chain with the given arguments and the same {@code this} pointer.
         *
         * @param args The arguments used for the method call
         * @return The result returned from next interceptor or the original method if current
         * interceptor is the last one in the chain. For void methods, always returns {@code null}.
         * @throws Throwable if any interceptor or the original method throws an exception
         */
        @Nullable
        Object proceed(Object... args) throws Throwable;

        /**
         * Proceeds to the next interceptor in the chain with the same arguments and given {@code this} pointer.
         * Static method interceptors should not call this.
         *
         * @param thisObject The {@code this} pointer for the method call
         * @return The result returned from next interceptor or the original method if current
         * interceptor is the last one in the chain. For void methods, always returns {@code null}.
         * @throws Throwable if any interceptor or the original method throws an exception
         */
        @Nullable
        Object proceedWith(@NonNull Object thisObject) throws Throwable;

        /**
         * Proceeds to the next interceptor in the chain with the given arguments and {@code this} pointer.
         * Static method interceptors should not call this.
         *
         * @param thisObject The {@code this} pointer for the method call
         * @param args       The arguments used for the method call
         * @return The result returned from next interceptor or the original method if current
         * interceptor is the last one in the chain. For void methods, always returns {@code null}.
         * @throws Throwable if any interceptor or the original method throws an exception
         */
        @Nullable
        Object proceedWith(@NonNull Object thisObject, Object... args) throws Throwable;
    }

    /**
     * Interceptor chain for a constructor.
     */
    interface CtorChain<T> extends Chain<Constructor<T>> {
        /**
         * Gets the instance being constructed. Note that the instance may be not fully initialized when
         * the chain is called.
         */
        @NonNull
        T getThisObject();

        /**
         * Proceeds to the next interceptor in the chain with the same arguments and {@code this} pointer.
         *
         * @throws Throwable if any interceptor or the original constructor throws an exception
         */
        void proceed() throws Throwable;

        /**
         * Proceeds to the next interceptor in the chain with the given arguments and the same {@code this} pointer.
         *
         * @param args The arguments used for the construction
         * @throws Throwable if any interceptor or the original constructor throws an exception
         */
        void proceed(Object... args) throws Throwable;

        /**
         * Proceeds to the next interceptor in the chain with the same arguments and given {@code this} pointer.
         *
         * @param thisObject The instance being constructed
         * @throws Throwable if any interceptor or the original constructor throws an exception
         */
        void proceedWith(@NonNull T thisObject) throws Throwable;

        /**
         * Proceeds to the next interceptor in the chain with the given arguments and {@code this} pointer.
         *
         * @param thisObject The instance being constructed
         * @param args       The arguments used for the construction
         * @throws Throwable if any interceptor or the original constructor throws an exception
         */
        void proceedWith(@NonNull T thisObject, Object... args) throws Throwable;
    }

    /**
     * Hooker for a method or constructor.
     *
     * @param <T> {@link Method} or {@link Constructor}
     */
    interface Hooker<T extends Executable> {
    }

    /**
     * Hooker for a method.
     */
    interface MethodHooker extends Hooker<Method> {
        /**
         * Intercepts a method call.
         *
         * @param chain The interceptor chain for the method call
         * @return The result to be returned from the interceptor. If the hooker does not want to
         * change the result, it should call {@code chain.proceed()} and return its result.
         * <p>For void methods, the return value is ignored by the framework.</p>
         * @throws Throwable Throw any exception from the interceptor. The exception will
         *                   propagate to the caller if not caught by any interceptor.
         */
        @Nullable
        Object intercept(@NonNull MethodChain chain) throws Throwable;
    }

    /**
     * Utility hooker for a void method. Used in Java lambda where unit return type is not supported.
     */
    @kotlin.Deprecated(message = "Hidden from Kotlin", level = kotlin.DeprecationLevel.HIDDEN)
    interface VoidMethodHooker extends Hooker<Method> {
        /**
         * Intercepts a method call.
         *
         * @param chain The interceptor chain for the method call
         * @throws Throwable Throw any exception from the interceptor. The exception will
         *                   propagate to the caller if not caught by any interceptor.
         */
        void intercept(@NonNull MethodChain chain) throws Throwable;
    }

    /**
     * Hooker for a constructor.
     */
    interface CtorHooker<T> extends Hooker<Constructor<T>> {
        /**
         * Intercepts a constructor call.
         *
         * @param chain The interceptor chain for the constructor call
         * @throws Throwable Throw any exception from the interceptor. The exception will
         *                   propagate to the caller if not caught by any interceptor.
         */
        void intercept(@NonNull CtorChain<T> chain) throws Throwable;
    }

    /**
     * Handle for a hook.
     *
     * @param <T> {@link Method} or {@link Constructor}
     */
    interface HookHandle<T extends Executable> {
        /**
         * Gets the method / constructor being hooked.
         */
        @NonNull
        T getExecutable();

        /**
         * Cancels the hook. This method is idempotent. It is safe to call this method multiple times.
         */
        void unhook();
    }

    /**
     * Builder for configuring a hook.
     *
     * @param <T> The concrete builder type for chaining
     * @param <U> {@link Method} or {@link Constructor}
     */
    interface HookBuilder<T extends HookBuilder<T, U>, U extends Executable> {
        /**
         * Sets the priority of the hook. Hooks with higher priority will be called before hooks with lower
         * priority. The default priority is {@link XposedInterface#PRIORITY_DEFAULT}.
         *
         * @param priority The priority of the hook
         * @return The builder itself for chaining
         */
        T setPriority(int priority);
    }

    /**
     * Builder for a method hook.
     */
    interface MethodHookBuilder extends HookBuilder<MethodHookBuilder, Method> {
        /**
         * Sets the hooker for the method and builds the hook.
         *
         * @param hooker The hooker object
         * @return The handle for the hook
         * @throws IllegalArgumentException if origin is abstract, framework internal or {@link Method#invoke},
         *                                  or hooker is invalid
         * @throws HookFailedError          if hook fails due to framework internal error
         */
        @NonNull
        HookHandle<Method> intercept(@NonNull MethodHooker hooker);

        /**
         * Sets a void hooker for the method and builds the hook. This is a utility method for Java
         * lambda where unit return type is not supported.
         *
         * @param hooker The hooker object
         * @return The handle for the hook
         * @throws IllegalArgumentException if origin is abstract, framework internal or {@link Method#invoke},
         *                                  or hooker is invalid
         * @throws HookFailedError          if hook fails due to framework internal error
         */
        @kotlin.Deprecated(message = "Hidden from Kotlin", level = kotlin.DeprecationLevel.HIDDEN)
        @NonNull
        HookHandle<Method> intercept(@NonNull VoidMethodHooker hooker);
    }

    /**
     * Builder for a constructor hook.
     *
     * @param <T> The type of the constructor
     */
    interface CtorHookBuilder<T> extends HookBuilder<CtorHookBuilder<T>, Constructor<T>> {
        /**
         * Sets the hooker for the constructor and builds the hook.
         *
         * @param hooker The hooker object
         * @return The handle for the hook
         * @throws IllegalArgumentException if origin is framework internal or {@link Constructor#newInstance},
         *                                  or hooker is invalid
         * @throws HookFailedError          if hook fails due to framework internal error
         */
        @NonNull
        HookHandle<Constructor<T>> intercept(@NonNull CtorHooker<T> hooker);
    }

    /**
     * Gets the Xposed API version. Framework implementations must <b>not</b> override this method.
     *
     * @return API version
     */
    default int getApiVersion() {
        return 101;
    }

    /**
     * Gets the Xposed framework name of current implementation.
     *
     * @return Framework name
     */
    @NonNull
    String getFrameworkName();

    /**
     * Gets the Xposed framework version of current implementation.
     *
     * @return Framework version
     */
    @NonNull
    String getFrameworkVersion();

    /**
     * Gets the Xposed framework version code of current implementation.
     *
     * @return Framework version code
     */
    long getFrameworkVersionCode();

    /**
     * Gets the Xposed framework capabilities.
     * Capabilities with prefix CAP_RT_ may change among launches.
     *
     * @return Framework capabilities
     */
    long getFrameworkCapabilities();

    /**
     * Hook a method.
     *
     * @param origin The method to be hooked
     * @return The builder for the hook
     */
    @NonNull
    MethodHookBuilder hook(@NonNull Method origin);

    /**
     * Hook a constructor.
     *
     * @param origin The constructor to be hooked
     * @return The builder for the hook
     */
    @NonNull
    <T> CtorHookBuilder<T> hook(@NonNull Constructor<T> origin);

    /**
     * Hook the static initializer of a class.
     * <p>
     * Note: If the class is initialized, the hook will never be called.
     * </p>
     *
     * @param origin The class to be hooked
     * @return The builder for the hook
     */
    @NonNull
    MethodHookBuilder hookClassInitializer(@NonNull Class<?> origin);

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
     * Get a method invoker for the given method. The default type of the invoker is
     * {@link Invoker.Type.Chain#FULL}.
     *
     * @param method The method to get the invoker for
     * @return The method invoker
     */
    @NonNull
    MethodInvoker getInvoker(@NonNull Method method);

    /**
     * Get a constructor invoker for the given constructor. The default type of the invoker is
     * {@link Invoker.Type.Chain#FULL}.
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
    ApplicationInfo getApplicationInfo();

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
}
