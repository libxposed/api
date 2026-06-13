package io.github.libxposed.api;

import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.os.ParcelFileDescriptor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;

import io.github.libxposed.annotation.InternalApi;
import io.github.libxposed.annotation.SinceApi;

/**
 * Wrapper of {@link XposedInterface} used by modules to shield framework implementation details.
 */
public class XposedInterfaceWrapper implements XposedInterface {

    private XposedInterface mBase;
    private Runnable mDetachImpl;

    /**
     * Attaches the framework interface to the module. Modules <b>must not</b> call this method.
     * It is reserved for framework implementations and may change without compatibility guarantees.
     *
     * @param base       The framework interface
     * @param detachImpl The implementation of {@link #detach()}
     */
    @InternalApi
    @SuppressWarnings("unused")
    public final void attachFramework(@NonNull XposedInterface base, @NonNull Runnable detachImpl) {
        if (mBase != null) {
            throw new IllegalStateException("Framework already attached");
        }
        mBase = base;
        mDetachImpl = detachImpl;
    }

    private void ensureAttached() {
        if (mBase == null) {
            throw new IllegalStateException("Framework not attached");
        }
    }

    /**
     * Stops all subsequent lifecycle callbacks for the <b>current module entry</b> in the current
     * process. After this method is called, the framework removes its reference to the entry
     * instance and will no longer invoke any lifecycle callbacks (such as
     * {@link XposedModuleInterface#onPackageLoaded},
     * {@link XposedModuleInterface#onHotReloading}, etc.) on the entry instance that
     * called this method. Only lifecycle callbacks are affected; all {@link XposedInterface} APIs
     * remain fully functional.
     *
     * <p>If the module declares multiple entry classes, only the entry that calls this method is
     * affected. Other entries continue to receive their lifecycle callbacks as normal.</p>
     *
     * <p>This method is idempotent. Calling it multiple times has the same effect as calling it once.</p>
     *
     * <p>If the module expects its classloader to become collectible after detaching, it must also
     * remove module-owned references and execution contexts that keep module objects reachable, such
     * as installed hooks, Java threads, and callbacks held by system or app objects. If native code
     * is still running after all Java references to the module classloader are cleared, later runtime
     * unloading of native libraries may crash the process; this is a module lifecycle bug.</p>
     *
     * <p>Typical use cases include:</p>
     * <ul>
     *     <li>The module entry has finished all its initialization work and no longer needs to
     *     respond to further package loading events.</li>
     *     <li>For modules that target multiple apps with a dedicated entry class per app: if the
     *     entry detects it is not loaded in its target app, it can call this method immediately to
     *     avoid receiving any further callbacks.</li>
     *     <li>Calling this method together with unhooking all registered hooks, so that the module
     *     classloader can be garbage collected when no longer needed.</li>
     * </ul>
     */
    @SinceApi(API_102)
    @SuppressWarnings("unused")
    public final void detach() {
        ensureAttached();
        mDetachImpl.run();
    }

    @Override
    public final int getApiVersion() {
        ensureAttached();
        return XposedInterface.super.getApiVersion();
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
    public final long getFrameworkProperties() {
        ensureAttached();
        return mBase.getFrameworkProperties();
    }

    @NonNull
    @Override
    public final HookBuilder hook(@NonNull Executable origin) {
        ensureAttached();
        return mBase.hook(origin);
    }

    @NonNull
    @Override
    public final HookBuilder hookClassInitializer(@NonNull Class<?> origin) {
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
    public final Invoker<?, Method> getInvoker(@NonNull Method method) {
        ensureAttached();
        return mBase.getInvoker(method);
    }

    @NonNull
    @Override
    public final <T> CtorInvoker<T> getInvoker(@NonNull Constructor<T> constructor) {
        ensureAttached();
        return mBase.getInvoker(constructor);
    }

    @Override
    public final void log(int priority, @Nullable String tag, @NonNull String msg) {
        ensureAttached();
        mBase.log(priority, tag, msg);
    }

    @Override
    public final void log(int priority, @Nullable String tag, @NonNull String msg, @Nullable Throwable tr) {
        ensureAttached();
        mBase.log(priority, tag, msg, tr);
    }

    @NonNull
    @Override
    public final SharedPreferences getRemotePreferences(@NonNull String name) {
        ensureAttached();
        return mBase.getRemotePreferences(name);
    }

    @NonNull
    @Override
    public final ApplicationInfo getModuleApplicationInfo() {
        ensureAttached();
        return mBase.getModuleApplicationInfo();
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
