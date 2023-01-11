package io.github.libxposed;

import androidx.annotation.NonNull;

/**
 * The type Xposed module.
 */
@SuppressWarnings("unused")
public abstract class XposedModule extends XposedContextWrapper implements XposedModuleInterface {
    /**
     * Instantiates a new Xposed module.
     *
     * @param base  the base
     * @param param the param
     */
    public XposedModule(XposedContext base, @NonNull ModuleLoadedParam param) {
        super(base);
    }

    @Override
    public void onPackageLoaded(@NonNull PackageLoadedParam param) {

    }

    @Override
    public void onSystemServerLoaded(@NonNull SystemServerLoadedParam param) {

    }
}
