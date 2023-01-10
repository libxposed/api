package io.github.libxposed;

import androidx.annotation.NonNull;

@SuppressWarnings("unused")
public abstract class XposedModule extends XposedContextWrapper implements XposedModuleInterface {
    public XposedModule(XposedContext base, @NonNull ModuleLoadedParam param) {
        super(base);
    }

    @Override
    public void onPackageLoaded(@NonNull PackageLoadedParam param) {

    }
}
