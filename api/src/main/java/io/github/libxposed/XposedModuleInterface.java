package io.github.libxposed;

import android.content.pm.ApplicationInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * The interface Xposed module interface.
 */
@SuppressWarnings("unused")
public interface XposedModuleInterface {
    /**
     * The interface Module loaded param.
     */
    interface ModuleLoadedParam {
        /**
         * Is system server boolean.
         *
         * @return the boolean
         */
        boolean isSystemServer();

        /**
         * Gets process name.
         *
         * @return the process name
         */
        @NonNull
        String getProcessName();

        /**
         * Gets app data dir.
         *
         * @return the app data dir
         */
        @NonNull
        String getAppDataDir();

        /**
         * Gets extras.
         *
         * @return the extras
         */
        @Nullable
        Bundle getExtras();
    }

    interface SystemServerLoadedParam {
        @NonNull
        ClassLoader getClassLoader();

        @Nullable
        Bundle getExtras();
    }

    /**
     * The interface Package loaded param.
     */
    interface PackageLoadedParam {
        /**
         * Gets package name.
         *
         * @return the package name
         */
        @NonNull
        String getPackageName();

        /**
         * Gets app info.
         *
         * @return the app info
         */
        @NonNull
        ApplicationInfo getAppInfo();

        /**
         * Gets class loader.
         *
         * @return the class loader
         */
        @NonNull
        ClassLoader getClassLoader();

        /**
         * Gets process name.
         *
         * @return the process name
         */
        @NonNull
        String getProcessName();

        /**
         * Is first application boolean.
         *
         * @return the boolean
         */
        boolean isFirstApplication();

        /**
         * Gets extras.
         *
         * @return the extras
         */
        @Nullable
        Bundle getExtras();
    }

    /**
     * On package loaded.
     *
     * @param param the param
     */
    void onPackageLoaded(@NonNull PackageLoadedParam param);

    void onSystemServerLoaded(@NonNull SystemServerLoadedParam param);
}
