package io.github.libxposed.api;

import android.content.Context;
import android.content.ContextWrapper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;

import io.github.libxposed.api.utils.DexParser;

/**
 * Wrap of {@link XposedContext} used by the modules for the purpose of shielding framework implementation details.
 */
@SuppressWarnings({"deprecation", "unused"})
public class XposedContextWrapper extends ContextWrapper implements XposedInterface {

    XposedContextWrapper(@NonNull XposedContext base) {
        super(base);
    }

    XposedContextWrapper(@NonNull XposedContextWrapper base) {
        super(base);
    }

    /**
     * Get the Xposed API version of current implementation.
     *
     * @return API version
     */
    final public int getAPIVersion() {
        return API;
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    final public XposedContext getBaseContext() {
        return (XposedContext) super.getBaseContext();
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    final public String getFrameworkName() {
        return getBaseContext().getFrameworkName();
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    final public String getFrameworkVersion() {
        return getBaseContext().getFrameworkVersion();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final public long getFrameworkVersionCode() {
        return getBaseContext().getFrameworkVersionCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final public int getFrameworkPrivilege() {
        return getBaseContext().getFrameworkPrivilege();
    }

    /**
     * {@inheritDoc}
     */
    @Deprecated
    @Nullable
    @Override
    final public Object featuredMethod(String name, Object... args) {
        return getBaseContext().featuredMethod(name, args);
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    final public MethodUnhooker<BeforeHooker<Method>, Method> hookBefore(@NonNull Method origin, @NonNull BeforeHooker<Method> hooker) {
        return getBaseContext().hookBefore(origin, hooker);
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    final public MethodUnhooker<AfterHooker<Method>, Method> hookAfter(@NonNull Method origin, @NonNull AfterHooker<Method> hooker) {
        return getBaseContext().hookAfter(origin, hooker);
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    final public MethodUnhooker<Hooker<Method>, Method> hook(@NonNull Method origin, @NonNull Hooker<Method> hooker) {
        return getBaseContext().hook(origin, hooker);
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    final public MethodUnhooker<BeforeHooker<Method>, Method> hookBefore(@NonNull Method origin, int priority, @NonNull BeforeHooker<Method> hooker) {
        return getBaseContext().hookBefore(origin, priority, hooker);
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    final public MethodUnhooker<AfterHooker<Method>, Method> hookAfter(@NonNull Method origin, int priority, @NonNull AfterHooker<Method> hooker) {
        return getBaseContext().hookAfter(origin, priority, hooker);
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    final public MethodUnhooker<Hooker<Method>, Method> hook(@NonNull Method origin, int priority, @NonNull Hooker<Method> hooker) {
        return getBaseContext().hook(origin, priority, hooker);
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    final public <T> MethodUnhooker<BeforeHooker<Constructor<T>>, Constructor<T>> hookBefore(@NonNull Constructor<T> origin, @NonNull BeforeHooker<Constructor<T>> hooker) {
        return getBaseContext().hookBefore(origin, hooker);
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    final public <T> MethodUnhooker<AfterHooker<Constructor<T>>, Constructor<T>> hookAfter(@NonNull Constructor<T> origin, @NonNull AfterHooker<Constructor<T>> hooker) {
        return getBaseContext().hookAfter(origin, hooker);
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    final public <T> MethodUnhooker<Hooker<Constructor<T>>, Constructor<T>> hook(@NonNull Constructor<T> origin, @NonNull Hooker<Constructor<T>> hooker) {
        return getBaseContext().hook(origin, hooker);
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    final public <T> MethodUnhooker<BeforeHooker<Constructor<T>>, Constructor<T>> hookBefore(@NonNull Constructor<T> origin, int priority, @NonNull BeforeHooker<Constructor<T>> hooker) {
        return getBaseContext().hookBefore(origin, priority, hooker);
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    final public <T> MethodUnhooker<AfterHooker<Constructor<T>>, Constructor<T>> hookAfter(@NonNull Constructor<T> origin, int priority, @NonNull AfterHooker<Constructor<T>> hooker) {
        return getBaseContext().hookAfter(origin, priority, hooker);
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    final public <T> MethodUnhooker<Hooker<Constructor<T>>, Constructor<T>> hook(@NonNull Constructor<T> origin, int priority, @NonNull Hooker<Constructor<T>> hooker) {
        return getBaseContext().hook(origin, priority, hooker);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final public boolean deoptimize(@NonNull Method method) {
        return getBaseContext().deoptimize(method);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final public <T> boolean deoptimize(@NonNull Constructor<T> constructor) {
        return getBaseContext().deoptimize(constructor);
    }

    @Nullable
    @Override
    public Object invokeOrigin(@NonNull Method method, @Nullable Object thisObject, Object... args) throws InvocationTargetException, IllegalArgumentException, IllegalAccessException {
        return getBaseContext().invokeOrigin(method, thisObject, args);
    }

    @Nullable
    @Override
    public Object invokeSpecial(@NonNull Method method, @NonNull Object thisObject, Object... args) throws InvocationTargetException, IllegalArgumentException, IllegalAccessException {
        return getBaseContext().invokeSpecial(method, thisObject, args);
    }

    @NonNull
    @Override
    public <T> T newInstanceOrigin(@NonNull Constructor<T> constructor, Object... args) throws InvocationTargetException, IllegalArgumentException, IllegalAccessException, InstantiationException {
        return getBaseContext().newInstanceOrigin(constructor, args);
    }

    @NonNull
    @Override
    public <T, U> U newInstanceSpecial(@NonNull Constructor<T> constructor, @NonNull Class<U> subClass, Object... args) throws InvocationTargetException, IllegalArgumentException, IllegalAccessException, InstantiationException {
        return getBaseContext().newInstanceSpecial(constructor, subClass, args);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final public void log(@NonNull String message) {
        getBaseContext().log(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final public void log(@NonNull String message, @NonNull Throwable throwable) {
        getBaseContext().log(message, throwable);
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    final public DexParser parseDex(@NonNull ByteBuffer dexData, boolean includeAnnotations) throws IOException {
        return getBaseContext().parseDex(dexData, includeAnnotations);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final protected void attachBaseContext(Context base) {
        if (base instanceof XposedContext || base instanceof XposedContextWrapper) {
            super.attachBaseContext(base);
        } else {
            throw new IllegalArgumentException();
        }
    }
}
