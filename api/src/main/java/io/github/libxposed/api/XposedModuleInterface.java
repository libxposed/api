package io.github.libxposed.api;

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
     * Wraps information about system server.
     */
    interface SystemServerLoadedParam {
        /**
         * Gets the class loader of system server.
         *
         * @return The class loader
         */
        @NonNull
        ClassLoader getClassLoader();
    }

    /**
     * Wraps information about the package being loaded.
     */
    interface PackageLoadedParam {
        /**
         * Gets the package name of the package being loaded.
         *
         * @return The package name.
         */
        @NonNull
        String getPackageName();

        /**
         * Gets the {@link ApplicationInfo} of the package being loaded.
         *
         * @return The ApplicationInfo.
         */
        @NonNull
        ApplicationInfo getApplicationInfo();

        /**
         * Gets default class loader.
         *
         * @return The default class loader
         */
        @RequiresApi(Build.VERSION_CODES.Q)
        @NonNull
        ClassLoader getDefaultClassLoader();

        /**
         * Gets the class loader of the package being loaded.
         *
         * @return The class loader.
         */
        @NonNull
        ClassLoader getClassLoader();

        /**
         * Returns whether this is the first and main package loaded in the app process.
         *
         * @return {@code true} if this is the first package.
         */
        boolean isFirstPackage();
    }

    /**
     * Gets notified when the module is loaded into the target process.<br/>
     * This callback is guaranteed to be called exactly once for a process before
     * {@link android.app.AppComponentFactory} is created.
     *
     * @param param Information about the process in which the module is loaded
     */
    default void onModuleLoaded(@NonNull ModuleLoadedParam param) {
    }

    /**
     * Gets notified when a package is loaded into the app process.<br/>
     * This callback could be invoked multiple times for the same process on each package.
     *
     * @param param Information about the package being loaded
     */
    default void onPackageLoaded(@NonNull PackageLoadedParam param) {
    }

    /**
     * Gets notified when the system server is loaded.
     *
     * @param param Information about system server
     */
    default void onSystemServerLoaded(@NonNull SystemServerLoadedParam param) {
    }
}
