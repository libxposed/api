package io.github.libxposed.api;

import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.os.ParcelFileDescriptor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;

import io.github.libxposed.api.utils.DexParser;

/**
 * Wrap of {@link XposedInterface} used by the modules for the purpose of shielding framework implementation details.
 */
public class XposedInterfaceWrapper implements XposedInterface {

    private XposedInterface mBase;

    /**
     * Attaches the framework interface to the module. Modules should never call this method.
     *
     * @param base The framework interface
     */
    @SuppressWarnings("unused")
    public final void attachFramework(@NonNull XposedInterface base) {
        mBase = base;
    }

    @NonNull
    @Override
    public final String getFrameworkName() {
        return mBase.getFrameworkName();
    }

    @NonNull
    @Override
    public final String getFrameworkVersion() {
        return mBase.getFrameworkVersion();
    }

    @Override
    public final long getFrameworkVersionCode() {
        return mBase.getFrameworkVersionCode();
    }

    @Override
    public final int getFrameworkPrivilege() {
        return mBase.getFrameworkPrivilege();
    }

    @NonNull
    @Override
    public final <T extends Executable> HookHandle<T> hook(@NonNull T origin, int priority, @NonNull Class<? extends Hooker> hooker) {
        return mBase.hook(origin, priority, hooker);
    }

    @NonNull
    @Override
    public <T> HookHandle<Constructor<T>> hookClassInitializer(@NonNull Class<T> origin, int priority, @NonNull Class<? extends Hooker> hooker) {
        return mBase.hookClassInitializer(origin, priority, hooker);
    }

    @Override
    public final boolean deoptimize(@NonNull Executable executable) {
        return mBase.deoptimize(executable);
    }

    @Nullable
    @Override
    public final Object invokeOrigin(@NonNull Method method, @Nullable Object thisObject, Object... args) throws InvocationTargetException, IllegalArgumentException, IllegalAccessException {
        return mBase.invokeOrigin(method, thisObject, args);
    }

    @Override
    public <T> void invokeOrigin(@NonNull Constructor<T> constructor, @NonNull T thisObject, Object... args) throws InvocationTargetException, IllegalArgumentException, IllegalAccessException {
        mBase.invokeOrigin(constructor, thisObject, args);
    }

    @NonNull
    @Override
    public final <T> T newInstanceOrigin(@NonNull Constructor<T> constructor, Object... args) throws InvocationTargetException, IllegalArgumentException, IllegalAccessException, InstantiationException {
        return mBase.newInstanceOrigin(constructor, args);
    }

    @Nullable
    @Override
    public final Object invokeSpecial(@NonNull Method method, @NonNull Object thisObject, Object... args) throws InvocationTargetException, IllegalArgumentException, IllegalAccessException {
        return mBase.invokeSpecial(method, thisObject, args);
    }

    @Override
    public <T> void invokeSpecial(@NonNull Constructor<T> constructor, @NonNull T thisObject, Object... args) throws InvocationTargetException, IllegalArgumentException, IllegalAccessException {
        mBase.invokeSpecial(constructor, thisObject, args);
    }

    @NonNull
    @Override
    public final <T, U> U newInstanceSpecial(@NonNull Constructor<T> constructor, @NonNull Class<U> subClass, Object... args) throws InvocationTargetException, IllegalArgumentException, IllegalAccessException, InstantiationException {
        return mBase.newInstanceSpecial(constructor, subClass, args);
    }

    @Override
    public final void log(@NonNull String message) {
        mBase.log(message);
    }

    @Override
    public final void log(@NonNull String message, @NonNull Throwable throwable) {
        mBase.log(message, throwable);
    }

    @Nullable
    @Override
    public final DexParser parseDex(@NonNull ByteBuffer dexData, boolean includeAnnotations) throws IOException {
        return mBase.parseDex(dexData, includeAnnotations);
    }

    @NonNull
    @Override
    public SharedPreferences getRemotePreferences(@NonNull String name) {
        return mBase.getRemotePreferences(name);
    }

    @NonNull
    @Override
    public ApplicationInfo getApplicationInfo() {
        return mBase.getApplicationInfo();
    }

    @NonNull
    @Override
    public String[] listRemoteFiles() {
        return mBase.listRemoteFiles();
    }

    @NonNull
    @Override
    public ParcelFileDescriptor openRemoteFile(@NonNull String name) throws FileNotFoundException {
        return mBase.openRemoteFile(name);
    }
}
