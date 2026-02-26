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
import java.lang.reflect.Method;
import java.nio.ByteBuffer;

import io.github.libxposed.api.utils.DexParser;

/**
 * Wrapper of {@link XposedInterface} used by modules to shield framework implementation details.
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
        if (mBase != null) {
            throw new IllegalStateException("Framework already attached");
        }
        mBase = base;
    }

    private void ensureAttached() {
        if (mBase == null) {
            throw new IllegalStateException("Framework not attached");
        }
    }

    @Override
    public final int getApiVersion() {
        ensureAttached();
        return mBase.getApiVersion();
    }

    @NonNull
    @Override
    public final String getFrameworkName() {
        ensureAttached();
        return mBase.getFrameworkName();
    }

    @NonNull
    @Override
    public final String getFrameworkVersion() {
        ensureAttached();
        return mBase.getFrameworkVersion();
    }

    @Override
    public final long getFrameworkVersionCode() {
        ensureAttached();
        return mBase.getFrameworkVersionCode();
    }

    @Override
    public final long getFrameworkCapabilities() {
        ensureAttached();
        return mBase.getFrameworkCapabilities();
    }

    @NonNull
    @Override
    public final MethodHookBuilder hook(@NonNull Method origin) {
        ensureAttached();
        return mBase.hook(origin);
    }

    @NonNull
    @Override
    public final <T> CtorHookBuilder<T> hook(@NonNull Constructor<T> origin) {
        ensureAttached();
        return mBase.hook(origin);
    }

    @NonNull
    @Override
    public final MethodHookBuilder hookClassInitializer(@NonNull Class<?> origin) {
        ensureAttached();
        return mBase.hookClassInitializer(origin);
    }

    @Override
    public final boolean deoptimize(@NonNull Executable executable) {
        ensureAttached();
        return mBase.deoptimize(executable);
    }

    @NonNull
    @Override
    public final MethodInvoker getInvoker(@NonNull Method method) {
        ensureAttached();
        return mBase.getInvoker(method);
    }

    @NonNull
    @Override
    public final MethodInvoker getInvoker(@NonNull Method method, @NonNull Invoker.Type type) {
        ensureAttached();
        return mBase.getInvoker(method, type);
    }

    @NonNull
    @Override
    public final <T> CtorInvoker<T> getInvoker(@NonNull Constructor<T> constructor) {
        ensureAttached();
        return mBase.getInvoker(constructor);
    }

    @NonNull
    @Override
    public final <T> CtorInvoker<T> getInvoker(@NonNull Constructor<T> constructor, @NonNull Invoker.Type type) {
        ensureAttached();
        return mBase.getInvoker(constructor, type);
    }

    @Override
    public final void log(int priority, @Nullable String tag, @NonNull String msg) {
        ensureAttached();
        mBase.log(priority, tag, msg, null);
    }

    @Override
    public final void log(int priority, @Nullable String tag, @NonNull String msg, @Nullable Throwable tr) {
        ensureAttached();
        mBase.log(priority, tag, msg, tr);
    }

    @Nullable
    @Override
    public final DexParser parseDex(@NonNull ByteBuffer dexData, boolean includeAnnotations) throws IOException {
        ensureAttached();
        return mBase.parseDex(dexData, includeAnnotations);
    }

    @NonNull
    @Override
    public final SharedPreferences getRemotePreferences(@NonNull String name) {
        ensureAttached();
        return mBase.getRemotePreferences(name);
    }

    @NonNull
    @Override
    public final ApplicationInfo getApplicationInfo() {
        ensureAttached();
        return mBase.getApplicationInfo();
    }

    @NonNull
    @Override
    public final String[] listRemoteFiles() {
        ensureAttached();
        return mBase.listRemoteFiles();
    }

    @NonNull
    @Override
    public final ParcelFileDescriptor openRemoteFile(@NonNull String name) throws FileNotFoundException {
        ensureAttached();
        return mBase.openRemoteFile(name);
    }
}
