package io.github.libxposed.api;

/**
 * Super class which all Xposed module entry classes should extend.<br/>
 * Entry classes will be instantiated exactly once for each process. Modules should not do initialization
 * work before {@link #onModuleLoaded(ModuleLoadedParam)} is called.
 */
@SuppressWarnings("unused")
public abstract class XposedModule extends XposedInterfaceWrapper implements XposedModuleInterface {
}
