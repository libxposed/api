package io.github.libxposed.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ConcurrentModificationException;

import io.github.libxposed.api.errors.HookFailedError;
import io.github.libxposed.api.utils.DexParser;

/**
 * Xposed interface for modules to operate on application processes.
 */
@SuppressWarnings("unused")
public interface XposedInterface {
    /**
     * SDK API version.
     */
    int API = 100;

    /**
     * Indicates that the framework is running as root.
     */
    int FRAMEWORK_PRIVILEGE_ROOT = 0;
    /**
     * Indicates that the framework is running in a container with a fake system_server.
     */
    int FRAMEWORK_PRIVILEGE_CONTAINER = 1;
    /**
     * Indicates that the framework is running as a different app, which may have at most shell permission.
     */
    int FRAMEWORK_PRIVILEGE_APP = 2;
    /**
     * Indicates that the framework is embedded in the hooked app,
     * which means {@link #getSharedPreferences} will be null and remote file is unsupported.
     */
    int FRAMEWORK_PRIVILEGE_EMBEDDED = 3;

    /**
     * The default hook priority.
     */
    int PRIORITY_DEFAULT = 50;
    /**
     * Execute the hook callback late.
     */
    int PRIORITY_LOWEST = -10000;
    /**
     * Execute the hook callback early.
     */
    int PRIORITY_HIGHEST = 10000;

    /**
     * The interface Before hook callback.
     *
     * @param <T> the type parameter
     */
    interface BeforeHookCallback<T> {
        /**
         * Gets origin.
         *
         * @return the origin
         */
        @NonNull
        T getOrigin();

        /**
         * Gets this.
         *
         * @return the this
         */
        @Nullable
        Object getThis();

        /**
         * Get args object [ ].
         *
         * @return the object [ ]
         */
        @NonNull
        Object[] getArgs();

        /**
         * Gets arg.
         *
         * @param <U>   the type parameter
         * @param index the index
         * @return the arg
         */
        @Nullable
        <U> U getArg(int index);

        /**
         * Sets arg.
         *
         * @param <U>   the type parameter
         * @param index the index
         * @param value the value
         */
        <U> void setArg(int index, U value);

        /**
         * Return and skip.
         *
         * @param returnValue the return value
         */
        void returnAndSkip(@Nullable Object returnValue);

        /**
         * Throw and skip.
         *
         * @param throwable the throwable
         */
        void throwAndSkip(@Nullable Throwable throwable);

        /**
         * Invoke origin object.
         *
         * @return the object
         * @throws InvocationTargetException the invocation target exception
         * @throws IllegalArgumentException  the illegal argument exception
         * @throws IllegalAccessException    the illegal access exception
         */
        @Nullable
        Object invokeOrigin() throws InvocationTargetException, IllegalArgumentException, IllegalAccessException;

        /**
         * Sets extra.
         *
         * @param <U>   the type parameter
         * @param key   the key
         * @param value the value
         * @throws ConcurrentModificationException the concurrent modification exception
         */
        <U> void setExtra(@NonNull String key, @Nullable U value) throws ConcurrentModificationException;
    }

    /**
     * The interface After hook callback.
     *
     * @param <T> the type parameter
     */
    interface AfterHookCallback<T> {
        /**
         * Gets origin.
         *
         * @return the origin
         */
        @NonNull
        T getOrigin();

        /**
         * Gets this.
         *
         * @return the this
         */
        @Nullable
        Object getThis();

        /**
         * Get args object [ ].
         *
         * @return the object [ ]
         */
        @NonNull
        Object[] getArgs();

        /**
         * Gets result.
         *
         * @return the result
         */
        @Nullable
        Object getResult();

        /**
         * Gets throwable.
         *
         * @return the throwable
         */
        @Nullable
        Throwable getThrowable();

        /**
         * Is skipped boolean.
         *
         * @return the boolean
         */
        boolean isSkipped();

        /**
         * Sets result.
         *
         * @param result the result
         */
        void setResult(@Nullable Object result);

        /**
         * Sets throwable.
         *
         * @param throwable the throwable
         */
        void setThrowable(@Nullable Throwable throwable);

        /**
         * Invoke origin object.
         *
         * @return the object
         * @throws InvocationTargetException the invocation target exception
         * @throws IllegalAccessException    the illegal access exception
         */
        @Nullable
        Object invokeOrigin() throws InvocationTargetException, IllegalAccessException;

        /**
         * Gets extra.
         *
         * @param <U> the type parameter
         * @param key the key
         * @return the extra
         */
        @Nullable
        <U> U getExtra(@NonNull String key);
    }

    /**
     * The interface Before hooker.
     *
     * @param <T> the type parameter
     */
    interface BeforeHooker<T> {
        /**
         * Before.
         *
         * @param callback the callback
         */
        void before(@NonNull BeforeHookCallback<T> callback);
    }

    /**
     * The interface After hooker.
     *
     * @param <T> the type parameter
     */
    interface AfterHooker<T> {
        /**
         * After.
         *
         * @param callback the callback
         */
        void after(@NonNull AfterHookCallback<T> callback);
    }

    /**
     * The interface Hooker.
     *
     * @param <T> the type parameter
     */
    interface Hooker<T> extends BeforeHooker<T>, AfterHooker<T> {
    }

    /**
     * The interface Method unhooker.
     *
     * @param <T> the type parameter
     * @param <U> the type parameter
     */
    interface MethodUnhooker<T, U> {
        /**
         * Gets origin.
         *
         * @return the origin
         */
        @NonNull
        U getOrigin();

        /**
         * Gets hooker.
         *
         * @return the hooker
         */
        @NonNull
        T getHooker();

        /**
         * Unhook.
         */
        void unhook();
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
     * Gets the Xposed framework privilege of current implementation.
     *
     * @return Framework privilege
     */
    int getFrameworkPrivilege();

    /**
     * Hook before method unhooker.
     *
     * @param origin the origin
     * @param hooker the hooker
     * @return the method unhooker
     * @throws IllegalArgumentException if origin is abstract, framework internal or {@link Method#invoke}
     * @throws HookFailedError          if hook fails due to framework internal error
     */
    @NonNull
    MethodUnhooker<BeforeHooker<Method>, Method> hookBefore(@NonNull Method origin, @NonNull BeforeHooker<Method> hooker);

    /**
     * Hook after method unhooker.
     *
     * @param origin the origin
     * @param hooker the hooker
     * @return the method unhooker
     * @throws IllegalArgumentException if origin is abstract, framework internal or {@link Method#invoke}
     * @throws HookFailedError          if hook fails due to framework internal error
     */
    @NonNull
    MethodUnhooker<AfterHooker<Method>, Method> hookAfter(@NonNull Method origin, @NonNull AfterHooker<Method> hooker);

    /**
     * Hook method unhooker.
     *
     * @param origin the origin
     * @param hooker the hooker
     * @return the method unhooker
     * @throws IllegalArgumentException if origin is abstract, framework internal or {@link Method#invoke}
     * @throws HookFailedError          if hook fails due to framework internal error
     */
    @NonNull
    MethodUnhooker<Hooker<Method>, Method> hook(@NonNull Method origin, @NonNull Hooker<Method> hooker);

    /**
     * Hook before method unhooker.
     *
     * @param origin   the origin
     * @param priority the priority
     * @param hooker   the hooker
     * @return the method unhooker
     * @throws IllegalArgumentException if origin is abstract, framework internal or {@link Method#invoke}
     * @throws HookFailedError          if hook fails due to framework internal error
     */
    @NonNull
    MethodUnhooker<BeforeHooker<Method>, Method> hookBefore(@NonNull Method origin, int priority, @NonNull BeforeHooker<Method> hooker);

    /**
     * Hook after method unhooker.
     *
     * @param origin   the origin
     * @param priority the priority
     * @param hooker   the hooker
     * @return the method unhooker
     * @throws IllegalArgumentException if origin is abstract, framework internal or {@link Method#invoke}
     * @throws HookFailedError          if hook fails due to framework internal error
     */
    @NonNull
    MethodUnhooker<AfterHooker<Method>, Method> hookAfter(@NonNull Method origin, int priority, @NonNull AfterHooker<Method> hooker);

    /**
     * Hook method unhooker.
     *
     * @param origin   the origin
     * @param priority the priority
     * @param hooker   the hooker
     * @return the method unhooker
     * @throws IllegalArgumentException if origin is abstract, framework internal or {@link Method#invoke}
     * @throws HookFailedError          if hook fails due to framework internal error
     */
    @NonNull
    MethodUnhooker<Hooker<Method>, Method> hook(@NonNull Method origin, int priority, @NonNull Hooker<Method> hooker);

    /**
     * Hook before method unhooker.
     *
     * @param <T>    the type parameter
     * @param origin the origin
     * @param hooker the hooker
     * @return the method unhooker
     * @throws IllegalArgumentException if origin is abstract, framework internal or {@link Method#invoke}
     * @throws HookFailedError          if hook fails due to framework internal error
     */
    @NonNull
    <T> MethodUnhooker<BeforeHooker<Constructor<T>>, Constructor<T>> hookBefore(@NonNull Constructor<T> origin, @NonNull BeforeHooker<Constructor<T>> hooker);

    /**
     * Hook after method unhooker.
     *
     * @param <T>    the type parameter
     * @param origin the origin
     * @param hooker the hooker
     * @return the method unhooker
     * @throws IllegalArgumentException if origin is abstract, framework internal or {@link Method#invoke}
     * @throws HookFailedError          if hook fails due to framework internal error
     */
    @NonNull
    <T> MethodUnhooker<AfterHooker<Constructor<T>>, Constructor<T>> hookAfter(@NonNull Constructor<T> origin, @NonNull AfterHooker<Constructor<T>> hooker);

    /**
     * Hook method unhooker.
     *
     * @param <T>    the type parameter
     * @param origin the origin
     * @param hooker the hooker
     * @return the method unhooker
     * @throws IllegalArgumentException if origin is abstract, framework internal or {@link Method#invoke}
     * @throws HookFailedError          if hook fails due to framework internal error
     */
    @NonNull
    <T> MethodUnhooker<Hooker<Constructor<T>>, Constructor<T>> hook(@NonNull Constructor<T> origin, @NonNull Hooker<Constructor<T>> hooker);

    /**
     * Hook before method unhooker.
     *
     * @param <T>      the type parameter
     * @param origin   the origin
     * @param priority the priority
     * @param hooker   the hooker
     * @return the method unhooker
     * @throws IllegalArgumentException if origin is abstract, framework internal or {@link Method#invoke}
     * @throws HookFailedError          if hook fails due to framework internal error
     */
    @NonNull
    <T> MethodUnhooker<BeforeHooker<Constructor<T>>, Constructor<T>> hookBefore(@NonNull Constructor<T> origin, int priority, @NonNull BeforeHooker<Constructor<T>> hooker);

    /**
     * Hook after method unhooker.
     *
     * @param <T>      the type parameter
     * @param origin   the origin
     * @param priority the priority
     * @param hooker   the hooker
     * @return the method unhooker
     * @throws IllegalArgumentException if origin is abstract, framework internal or {@link Method#invoke}
     * @throws HookFailedError          if hook fails due to framework internal error
     */
    @NonNull
    <T> MethodUnhooker<AfterHooker<Constructor<T>>, Constructor<T>> hookAfter(@NonNull Constructor<T> origin, int priority, @NonNull AfterHooker<Constructor<T>> hooker);

    /**
     * Hook method unhooker.
     *
     * @param <T>      the type parameter
     * @param origin   the origin
     * @param priority the priority
     * @param hooker   the hooker
     * @return the method unhooker
     * @throws IllegalArgumentException if origin is abstract, framework internal or {@link Method#invoke}
     * @throws HookFailedError          if hook fails due to framework internal error
     */
    @NonNull
    <T> MethodUnhooker<Hooker<Constructor<T>>, Constructor<T>> hook(@NonNull Constructor<T> origin, int priority, @NonNull Hooker<Constructor<T>> hooker);

    /**
     * Deoptimizes a method in case hooked callee is not called because of inline.
     *
     * <p>By deoptimizing the method, the method will back all callee without inlining.
     * For example, when a short hooked method B is invoked by method A, the callback to B is not invoked
     * after hooking, which may mean A has inlined B inside its method body. To force A to call the hooked B,
     * you can deoptimize A and then your hook can take effect.</p>
     *
     * <p>Generally, you need to find all the callers of your hooked callee and that can be hardly achieve
     * (but you can still search all callers by using {@link DexParser}). Use this method if you are sure
     * the deoptimized callers are all you need. Otherwise, it would be better to change the hook point or
     * to deoptimize the whole app manually (by simply reinstalling the app without uninstall).</p>
     *
     * @param method The method to deoptimize
     * @return Indicate whether the deoptimizing succeed or not
     */
    boolean deoptimize(@NonNull Method method);

    /**
     * Deoptimizes a constructor in case hooked callee is not called because of inline.
     *
     * @param <T>         The type of the constructor
     * @param constructor The constructor to deoptimize
     * @return Indicate whether the deoptimizing succeed or not
     * @see #deoptimize(Method)
     */
    <T> boolean deoptimize(@NonNull Constructor<T> constructor);

    /**
     * Basically the same as {@link Method#invoke(Object, Object...)}, but calls the original method
     * as it was before the interception by Xposed.
     *
     * @param method     The method to be called
     * @param thisObject For non-static calls, the {@code this} pointer, otherwise {@code null}
     * @param args       The arguments used for the method call
     * @return The result returned from the invoked method
     * @see Method#invoke(Object, Object...)
     */
    @Nullable
    Object invokeOrigin(@NonNull Method method, @Nullable Object thisObject, Object... args) throws InvocationTargetException, IllegalArgumentException, IllegalAccessException;

    /**
     * Invokes a special (non-virtual) method on a given object instance, similar to the functionality of
     * {@code CallNonVirtual<type>Method} in JNI, which invokes an instance (nonstatic) method on a Java
     * object. This method is useful when you need to call a specific method on an object, bypassing any
     * overridden methods in subclasses and directly invoking the method defined in the specified class.
     *
     * <p>This method is useful when you need to call {@code super.xxx()} in a hooked constructor.</p>
     *
     * @param method     The method to be called
     * @param thisObject For non-static calls, the {@code this} pointer, otherwise {@code null}
     * @param args       The arguments used for the method call
     * @return The result returned from the invoked method
     * @see Method#invoke(Object, Object...)
     */
    @Nullable
    Object invokeSpecial(@NonNull Method method, @NonNull Object thisObject, Object... args) throws InvocationTargetException, IllegalArgumentException, IllegalAccessException;

    /**
     * Basically the same as {@link Constructor#newInstance(Object...)}, but calls the original constructor
     * as it was before the interception by Xposed.
     *
     * @param <T>         The type of the constructor
     * @param constructor The constructor to create and initialize a new instance
     * @param args        The arguments used for the construction
     * @return The instance created and initialized by the constructor
     * @see Constructor#newInstance(Object...)
     */
    @NonNull
    <T> T newInstanceOrigin(@NonNull Constructor<T> constructor, Object... args) throws InvocationTargetException, IllegalArgumentException, IllegalAccessException, InstantiationException;

    /**
     * Creates a new instance of the given subclass, but initialize it with a parent constructor. This could
     * leave the object in an invalid state, where the subclass constructor are not called and the fields
     * of the subclass are not initialized.
     *
     * <p>This method is useful when you need to initialize some fields in the subclass by yourself.</p>
     *
     * @param <T>         The type of the parent constructor
     * @param <U>         The type of the subclass
     * @param constructor The parent constructor to initialize a new instance
     * @param subClass    The subclass to create a new instance
     * @param args        The arguments used for the construction
     * @return The instance of subclass initialized by the constructor
     * @see Constructor#newInstance(Object...)
     */
    @NonNull
    <T, U> U newInstanceSpecial(@NonNull Constructor<T> constructor, @NonNull Class<U> subClass, Object... args) throws InvocationTargetException, IllegalArgumentException, IllegalAccessException, InstantiationException;

    /**
     * Writes a message to the Xposed log.
     *
     * @param message The log message
     */
    void log(@NonNull String message);

    /**
     * Writes a message with a stack trace to the Xposed log.
     *
     * @param message   The log message
     * @param throwable The Throwable object for the stack trace
     */
    void log(@NonNull String message, @NonNull Throwable throwable);

    /**
     * Parse a dex file in memory.
     *
     * @param dexData            The content of the dex file
     * @param includeAnnotations Whether to include annotations
     * @return The {@link DexParser} of the dex file
     * @throws IOException if the dex file is invalid
     */
    @Nullable
    DexParser parseDex(@NonNull ByteBuffer dexData, boolean includeAnnotations) throws IOException;


    // Methods the same with Context

    /**
     * Gets remote preferences stored in Xposed framework. Note that those are read-only in hooked apps.
     *
     * @see Context#getSharedPreferences(String, int)
     */
    SharedPreferences getSharedPreferences(String name, int mode);

    /**
     * Open a remote file stored in Xposed framework.
     *
     * @see Context#openFileInput(String)
     */
    FileInputStream openFileInput(String name) throws FileNotFoundException;

    /**
     * List all remote files stored in Xposed framework. Note that you can only access files created by
     * your own module app with XposedService.
     *
     * @see Context#fileList()
     */
    String[] fileList();

    /**
     * Gets resources of the module.
     *
     * @see Context#getResources()
     */
    Resources getResources();

    /**
     * Gets the application info of the module.
     *
     * @see Context#getApplicationInfo()
     */
    ApplicationInfo getApplicationInfo();
}
