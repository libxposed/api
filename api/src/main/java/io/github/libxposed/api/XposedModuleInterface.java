package io.github.libxposed.api;

import android.app.AppComponentFactory;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.List;

import io.github.libxposed.annotation.SinceApi;

/**
 * Interface for module initialization.
 */
@SuppressWarnings("unused")
public interface XposedModuleInterface {
    /**
     * Wraps information about the process in which the module is loaded.
     * This information only indicates the state at the time of loading and will not be updated.
     */
    interface ModuleLoadedParam {
        /**
         * Returns whether the current process is system server.
         *
         * @return {@code true} if the current process is system server
         */
        boolean isSystemServer();

        /**
         * Gets the process name.
         *
         * @return The process name
         */
        @NonNull
        String getProcessName();
    }

    /**
     * Wraps information about the package being loaded.
     * This information only indicates the state at the time of loading and will not be updated.
     */
    interface PackageLoadedParam {
        /**
         * Gets the package name of the current package.
         *
         * @return The package name.
         */
        @NonNull
        String getPackageName();

        /**
         * Gets the {@link ApplicationInfo} of the current package.
         *
         * @return The ApplicationInfo.
         */
        @NonNull
        ApplicationInfo getApplicationInfo();

        /**
         * Returns whether this is the first and main package loaded in the process.
         *
         * @return {@code true} if this is the first package.
         */
        boolean isFirstPackage();

        /**
         * Gets the default classloader of the current package. This is the classloader that loads
         * the package's code, resources and custom {@link AppComponentFactory}.
         */
        @RequiresApi(Build.VERSION_CODES.Q)
        @NonNull
        ClassLoader getDefaultClassLoader();
    }

    /**
     * Wraps information about the package whose classloader is ready.
     * This information only indicates the state at the time of loading and will not be updated.
     */
    interface PackageReadyParam extends PackageLoadedParam {
        /**
         * Gets the classloader of the current package. It may be different from
         * {@link #getDefaultClassLoader()} if the package has a custom {@link AppComponentFactory}
         * that creates a different classloader.
         */
        @NonNull
        ClassLoader getClassLoader();

        /**
         * Gets the {@link AppComponentFactory} of the current package.
         */
        @RequiresApi(Build.VERSION_CODES.P)
        @NonNull
        AppComponentFactory getAppComponentFactory();
    }

    /**
     * Wraps information about system server.
     * This information only indicates the state at the time of loading and will not be updated.
     */
    interface SystemServerStartingParam {
        /**
         * Gets the class loader of system server.
         */
        @NonNull
        ClassLoader getClassLoader();
    }

    /**
     * Wraps information about the hot reloading event.
     */
    @SinceApi(XposedInterface.API_102)
    interface HotReloadingParam {
        /**
         * Gets the data passed from the module app when triggering hot reload through the service.
         * This can be null if the app passes {@code null} or the hot reload is triggered by app updating.
         */
        @Nullable
        Bundle getData();

        /**
         * Sets the data to be passed to the new code after hot reloading. This can be retrieved in {@link #onHotReloaded(HotReloadedParam)}.
         * Objects created under the system, system server, or app classloaders can be used. Be careful
         * that you should <b>NEVER</b> put an object that is created under the old module classloader, or
         * the old code may remain strongly reachable after hot reloading. If needed, you should use
         * {@link Bundle} to serialize the data.
         *
         * @param outState The data to be passed to the new code after hot reloading
         */
        void setSavedInstanceState(@Nullable Object outState);
    }

    /**
     * Wraps information about the hot reloaded event.
     */
    @SinceApi(XposedInterface.API_102)
    interface HotReloadedParam extends ModuleLoadedParam {
        /**
         * Gets the data passed from the module app when triggering hot reload. This can be null if the
         * app passes {@code null} or the hot reload is triggered by app updating.
         */
        @Nullable
        Bundle getData();

        /**
         * Gets the data set in {@link HotReloadingParam#setSavedInstanceState(Object)}.
         * This can be {@code null} if no data is set.
         */
        @Nullable
        Object getSavedInstanceState();

        /**
         * Gets a list of hook handles created by the old code. The new code can choose to remove or
         * atomically replace these hooks with new ones through
         * {@link XposedInterface.HookHandle#replaceHook(XposedInterface.Hooker)}.
         */
        @NonNull
        List<XposedInterface.HookHandle> getOldHookHandles();
    }

    /**
     * Gets notified when a module generation is loaded into the target process.
     * <p>
     * This callback is called for the initial module load. The default implementation of
     * {@link #onHotReloaded(HotReloadedParam)} also delegates to this method for the new generation
     * after unhooking old hooks. Modules that need reload-specific behavior should override
     * {@link #onHotReloaded(HotReloadedParam)}.
     *
     * @param param Information about the process in which the module is loaded
     * @throws RuntimeException Everything the callback throws is caught and logged.
     */
    default void onModuleLoaded(@NonNull ModuleLoadedParam param) {
    }

    /**
     * Gets notified when a {@link android.R.attr#hasCode} package is loaded into the process.
     * This is the time when the default classloader is ready but before the instantiation of
     * {@link AppComponentFactory}.
     * <p>
     * This callback is invoked only once for each package name loaded into the process,
     * note that a process may load multiple packages, such as {@link android.R.attr#sharedUserId}
     * and {@link Context#createPackageContext(String, int)} with {@link Context#CONTEXT_INCLUDE_CODE}.
     * <p>
     * In system server, the first callback is replaced by
     * {@link #onSystemServerStarting(SystemServerStartingParam)}, so
     * {@code param.isFirstPackage()} is never {@code true} here.
     *
     * @param param Information about the package being loaded
     * @throws RuntimeException Everything the callback throws is caught and logged.
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    default void onPackageLoaded(@NonNull PackageLoadedParam param) {
    }

    /**
     * Gets notified when {@link AppComponentFactory} has instantiated the classloader
     * and is ready to create {@link android.app.Application}.
     * <p>
     * This callback is invoked only once for each package name loaded into the process,
     * note that a process may load multiple packages, such as {@link android.R.attr#sharedUserId}
     * and {@link Context#createPackageContext(String, int)} with {@link Context#CONTEXT_INCLUDE_CODE}.
     * <p>
     * In system server, the first callback is replaced by
     * {@link #onSystemServerStarting(SystemServerStartingParam)}, so
     * {@code param.isFirstPackage()} is never {@code true} here.
     *
     * @param param Information about the package being loaded
     * @throws RuntimeException Everything the callback throws is caught and logged.
     */
    default void onPackageReady(@NonNull PackageReadyParam param) {
    }

    /**
     * Gets notified when system server is ready to start critical services.
     * In system server, this callback replaces the first callback phase of
     * {@link #onPackageLoaded(PackageLoadedParam)} and
     * {@link #onPackageReady(PackageReadyParam)}.
     *
     * @param param Information about system server
     * @throws RuntimeException Everything the callback throws is caught and logged.
     */
    default void onSystemServerStarting(@NonNull SystemServerStartingParam param) {
    }

    /**
     * Gets notified when the module is about to be reloaded. This callback is called when hot
     * reloading is triggered through the service, or by app updating if {@code autoHotReload} is set
     * to true in {@code module.prop}. App-update hot reloading still proceeds only if this callback
     * returns {@code true}.
     * <p>This callback runs in <b>old</b> code.</p>
     *
     * @param param Information about the hot reloading event
     * @return {@code true} to allow hot reloading to proceed, {@code false} to cancel hot reloading
     */
    @SinceApi(XposedInterface.API_102)
    default boolean onHotReloading(@NonNull HotReloadingParam param) {
        return false;
    }

    /**
     * Gets notified when the module has been reloaded.
     * <p>This callback runs in <b>new</b> code.</p>
     * <p>The default implementation unhooks all old hooks and then calls
     * {@link #onModuleLoaded(ModuleLoadedParam)}. Override this method to atomically replace old
     * hooks through {@link XposedInterface.HookHandle#replaceHook(XposedInterface.Hooker)}, remove
     * hooks that should not survive, or perform reload-specific initialization.</p>
     *
     * @param param Information about the hot reloaded event
     */
    @SinceApi(XposedInterface.API_102)
    default void onHotReloaded(@NonNull HotReloadedParam param) {
        param.getOldHookHandles().forEach(XposedInterface.HookHandle::unhook);
        onModuleLoaded(param);
    }
}
