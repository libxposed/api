package io.github.libxposed.api;

import android.app.AppComponentFactory;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

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
     * Gets notified when the module is loaded into the target process.<br/>
     * This callback is guaranteed to be called exactly once for a process.
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
}
