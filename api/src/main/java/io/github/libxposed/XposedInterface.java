package io.github.libxposed;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ConcurrentModificationException;

import io.github.libxposed.utils.DexParser;

/**
 * The interface Xposed interface.
 */
@SuppressWarnings("unused")
public interface XposedInterface {
    /**
     * The constant API.
     */
    int API = 100;

    /**
     * The constant FRAMEWORK_PRIVILEGE_ROOT.
     */
    int FRAMEWORK_PRIVILEGE_ROOT = 0;
    /**
     * The constant FRAMEWORK_PRIVILEGE_CONTAINER.
     */
    int FRAMEWORK_PRIVILEGE_CONTAINER = 1;
    /**
     * The constant FRAMEWORK_PRIVILEGE_APP.
     */
    int FRAMEWORK_PRIVILEGE_APP = 2;
    /**
     * The constant FRAMEWORK_PRIVILEGE_EMBEDDED.
     */
    int FRAMEWORK_PRIVILEGE_EMBEDDED = 3;

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
         * @param thisObject the this object
         * @param args       the args
         * @return the object
         * @throws InvocationTargetException the invocation target exception
         * @throws IllegalAccessException    the illegal access exception
         */
        @Nullable
        Object invokeOrigin(@Nullable Object thisObject, Object[] args) throws InvocationTargetException, IllegalAccessException;

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
         * @param thisObject the this object
         * @param args       the args
         * @return the object
         * @throws InvocationTargetException the invocation target exception
         * @throws IllegalAccessException    the illegal access exception
         */
        @Nullable
        Object invokeOrigin(@Nullable Object thisObject, Object[] args) throws InvocationTargetException, IllegalAccessException;

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
     * Gets framework name.
     *
     * @return the framework name
     */
    @NonNull
    String getFrameworkName();

    /**
     * Gets framework version.
     *
     * @return the framework version
     */
    @NonNull
    String getFrameworkVersion();

    /**
     * Gets framework version code.
     *
     * @return the framework version code
     */
    long getFrameworkVersionCode();

    /**
     * Gets framework privilege.
     *
     * @return the framework privilege
     */
    int getFrameworkPrivilege();

    /**
     * Featured method object.
     *
     * @param name the name
     * @param args the args
     * @return the object
     */
    Object featuredMethod(String name, Object... args);

    /**
     * Hook before method unhooker.
     *
     * @param origin the origin
     * @param hooker the hooker
     * @return the method unhooker
     */
    @Nullable
    MethodUnhooker<BeforeHooker<Method>, Method> hookBefore(@NonNull Method origin, @NonNull BeforeHooker<Method> hooker);

    /**
     * Hook after method unhooker.
     *
     * @param origin the origin
     * @param hooker the hooker
     * @return the method unhooker
     */
    @Nullable
    MethodUnhooker<AfterHooker<Method>, Method> hookAfter(@NonNull Method origin, @NonNull AfterHooker<Method> hooker);

    /**
     * Hook method unhooker.
     *
     * @param origin the origin
     * @param hooker the hooker
     * @return the method unhooker
     */
    @Nullable
    MethodUnhooker<Hooker<Method>, Method> hook(@NonNull Method origin, @NonNull Hooker<Method> hooker);

    /**
     * Hook before method unhooker.
     *
     * @param origin   the origin
     * @param priority the priority
     * @param hooker   the hooker
     * @return the method unhooker
     */
    @Nullable
    MethodUnhooker<BeforeHooker<Method>, Method> hookBefore(@NonNull Method origin, int priority, @NonNull BeforeHooker<Method> hooker);

    /**
     * Hook after method unhooker.
     *
     * @param origin   the origin
     * @param priority the priority
     * @param hooker   the hooker
     * @return the method unhooker
     */
    @Nullable
    MethodUnhooker<AfterHooker<Method>, Method> hookAfter(@NonNull Method origin, int priority, @NonNull AfterHooker<Method> hooker);

    /**
     * Hook method unhooker.
     *
     * @param origin   the origin
     * @param priority the priority
     * @param hooker   the hooker
     * @return the method unhooker
     */
    @Nullable
    MethodUnhooker<Hooker<Method>, Method> hook(@NonNull Method origin, int priority, @NonNull Hooker<Method> hooker);

    /**
     * Hook before method unhooker.
     *
     * @param <T>    the type parameter
     * @param origin the origin
     * @param hooker the hooker
     * @return the method unhooker
     */
    @Nullable
    <T> MethodUnhooker<BeforeHooker<Constructor<T>>, Constructor<T>> hookBefore(@NonNull Constructor<T> origin, @NonNull BeforeHooker<Constructor<T>> hooker);

    /**
     * Hook after method unhooker.
     *
     * @param <T>    the type parameter
     * @param origin the origin
     * @param hooker the hooker
     * @return the method unhooker
     */
    @Nullable
    <T> MethodUnhooker<AfterHooker<Constructor<T>>, Constructor<T>> hookAfter(@NonNull Constructor<T> origin, @NonNull AfterHooker<Constructor<T>> hooker);

    /**
     * Hook method unhooker.
     *
     * @param <T>    the type parameter
     * @param origin the origin
     * @param hooker the hooker
     * @return the method unhooker
     */
    @Nullable
    <T> MethodUnhooker<Hooker<Constructor<T>>, Constructor<T>> hook(@NonNull Constructor<T> origin, @NonNull Hooker<Constructor<T>> hooker);

    /**
     * Hook before method unhooker.
     *
     * @param <T>      the type parameter
     * @param origin   the origin
     * @param priority the priority
     * @param hooker   the hooker
     * @return the method unhooker
     */
    @Nullable
    <T> MethodUnhooker<BeforeHooker<Constructor<T>>, Constructor<T>> hookBefore(@NonNull Constructor<T> origin, int priority, @NonNull BeforeHooker<Constructor<T>> hooker);

    /**
     * Hook after method unhooker.
     *
     * @param <T>      the type parameter
     * @param origin   the origin
     * @param priority the priority
     * @param hooker   the hooker
     * @return the method unhooker
     */
    @Nullable
    <T> MethodUnhooker<AfterHooker<Constructor<T>>, Constructor<T>> hookAfter(@NonNull Constructor<T> origin, int priority, @NonNull AfterHooker<Constructor<T>> hooker);

    /**
     * Hook method unhooker.
     *
     * @param <T>      the type parameter
     * @param origin   the origin
     * @param priority the priority
     * @param hooker   the hooker
     * @return the method unhooker
     */
    @Nullable
    <T> MethodUnhooker<Hooker<Constructor<T>>, Constructor<T>> hook(@NonNull Constructor<T> origin, int priority, @NonNull Hooker<Constructor<T>> hooker);

    /**
     * Deoptimize boolean.
     *
     * @param method the method
     * @return the boolean
     */
    boolean deoptimize(@NonNull Method method);

    /**
     * Deoptimize boolean.
     *
     * @param <T>         the type parameter
     * @param constructor the constructor
     * @return the boolean
     */
    <T> boolean deoptimize(@NonNull Constructor<T> constructor);

    /**
     * Log.
     *
     * @param message the message
     */
    void log(@NonNull String message);

    /**
     * Log.
     *
     * @param message   the message
     * @param throwable the throwable
     */
    void log(@NonNull String message, @NonNull Throwable throwable);

    /**
     * Parse dex dex parser.
     *
     * @param dexData            the dex data
     * @param includeAnnotations the include annotations
     * @return the dex parser
     * @throws IOException the io exception
     */
    @Nullable
    DexParser parseDex(@NonNull ByteBuffer dexData, boolean includeAnnotations) throws IOException;
}
