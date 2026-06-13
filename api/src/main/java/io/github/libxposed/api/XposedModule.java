package io.github.libxposed.api;

/**
 * Super class which all Xposed module entry classes should extend.<br/>
 * Entry classes will be instantiated once for each loaded module generation in a process.
 */
@SuppressWarnings("unused")
public abstract class XposedModule extends XposedInterfaceWrapper implements XposedModuleInterface {
}
