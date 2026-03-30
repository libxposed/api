/**
 * Modern Xposed Module API.
 *
 * <p>This package provides the public API for developing Xposed modules using the modern
 * Xposed framework. It replaces the legacy XposedBridge API with a redesigned, type-safe
 * interface.</p>
 *
 * <h2>Getting Started</h2>
 *
 * <p>Module entry classes should extend {@link io.github.libxposed.api.XposedModule}. The
 * framework calls {@link io.github.libxposed.api.XposedInterfaceWrapper#attachFramework(XposedInterface)
 * attachFramework()} automatically; modules <b>should not</b> perform initialization work before
 * {@link io.github.libxposed.api.XposedModuleInterface#onModuleLoaded(XposedModuleInterface.ModuleLoadedParam)
 * onModuleLoaded()} is called.</p>
 *
 * <h2>Entry Registration</h2>
 *
 * <p>Java entry classes are listed in {@code META-INF/xposed/java_init.list} (one fully-qualified
 * class name per line); native entries use {@code META-INF/xposed/native_init.list}. Place these
 * files under {@code src/main/resources/META-INF/xposed/} and Gradle will package them into the
 * APK automatically.</p>
 *
 * <h2>Module Configuration</h2>
 *
 * <p>Module metadata is specified via standard Android resources ({@code android:label} for the
 * module name, {@code android:description} for the description) and
 * {@code META-INF/xposed/module.prop} (Java {@link java.util.Properties} format). Required
 * properties:</p>
 * <ul>
 *     <li>{@code minApiVersion} – minimum Xposed API version required</li>
 *     <li>{@code targetApiVersion} – target Xposed API version</li>
 * </ul>
 * <p>Optional properties:</p>
 * <ul>
 *     <li>{@code staticScope} (boolean) – whether the module scope is fixed and users should not
 *     apply the module on apps outside the scope list</li>
 *     <li>{@code exceptionMode} (string) [protective|passthrough] - Default to protective, see
 *     {@link io.github.libxposed.api.XposedInterface.ExceptionMode}</li>
 * </ul>
 *
 * <h2>Scope</h2>
 *
 * <p>Module scope is defined by a list of package names in {@code META-INF/xposed/scope.list}.
 * The framework injects the module into all regular processes declared by those packages.
 * After injection, every loaded package in that process will trigger callbacks.
 * As a result, modules may receive callbacks beyond the originally scoped packages, so they
 * should always filter by process name and package name.</p>
 *
 * <p>A special case applies to components declared with {@code android:process="system"} in a
 * {@code android:sharedUserId="android.uid.system"} package: they run in system server.
 * For packages whose components all run in system server, adding the package name itself to the
 * scope has no effect. Instead, system server is represented by the special virtual package name
 * {@code system}, which should be used explicitly. </p>
 *
 * <p>Note that {@code android} package is still a valid scope target because some of its
 * components declare {@code android:process=":ui"} and therefore do not run in system server.
 * Modules scoped to {@code android} can still receive events for {@code android} package loading
 * even though {@code android} package has no code. By contrast, {@code com.android.providers.settings}
 * is not a valid scope target, modules should use the {@code system} scope and then wait
 * for the {@code com.android.providers.settings} package loading event.</p>
 *
 * <h2>Hook Model</h2>
 *
 * <p>The API uses an <b>interceptor-chain</b> model (similar to OkHttp interceptors). Modules
 * implement {@link io.github.libxposed.api.XposedInterface.Hooker Hooker} and its
 * {@link io.github.libxposed.api.XposedInterface.Hooker#intercept(XposedInterface.Chain)
 * intercept(Chain)} method. Hooking is performed through a builder returned by
 * {@link io.github.libxposed.api.XposedInterface#hook(java.lang.reflect.Executable)
 * hook(Executable)}:</p>
 * <pre>{@code
 * HookHandle handle = hook(method)
 *     .setPriority(PRIORITY_DEFAULT)
 *     .setExceptionMode(ExceptionMode.PROTECTIVE)
 *     .intercept(chain -> {
 *         // pre-processing
 *         Object result = chain.proceed();
 *         // post-processing
 *         return result;
 *     });
 * }</pre>
 *
 * <h2>Invoker System</h2>
 *
 * <p>To call the original (or hooked) method bypassing access checks, obtain an
 * {@link io.github.libxposed.api.XposedInterface.Invoker Invoker} via
 * {@link io.github.libxposed.api.XposedInterface#getInvoker(java.lang.reflect.Method)
 * getInvoker(Method)} or
 * {@link io.github.libxposed.api.XposedInterface#getInvoker(java.lang.reflect.Constructor)
 * getInvoker(Constructor)}. The invoker type controls what part of the hook chain is executed
 * (see {@link io.github.libxposed.api.XposedInterface.Invoker.Type Invoker.Type}).</p>
 *
 * <h2>Module Lifecycle Callbacks</h2>
 *
 * <p>Override the following callbacks in {@link io.github.libxposed.api.XposedModule}:</p>
 * <ul>
 *     <li>{@link io.github.libxposed.api.XposedModuleInterface#onModuleLoaded(XposedModuleInterface.ModuleLoadedParam)
 *     onModuleLoaded()} – called once when the module is loaded into the target process.</li>
 *     <li>{@link io.github.libxposed.api.XposedModuleInterface#onPackageLoaded(XposedModuleInterface.PackageLoadedParam)
 *     onPackageLoaded()} – called when the default classloader is ready, before
 *     {@link android.app.AppComponentFactory} instantiation (API 29+).</li>
 *     <li>{@link io.github.libxposed.api.XposedModuleInterface#onPackageReady(XposedModuleInterface.PackageReadyParam)
 *     onPackageReady()} – called after the app classloader is created.</li>
 *     <li>{@link io.github.libxposed.api.XposedModuleInterface#onSystemServerStarting(XposedModuleInterface.SystemServerStartingParam)
 *     onSystemServerStarting()} – called once when system server is starting. This callback
 *     replaces the first package load phase.</li>
 * </ul>
 *
 * <h2>Error Handling</h2>
 *
 * <p>Framework-level errors are reported via subclasses of
 * {@link io.github.libxposed.api.error.XposedFrameworkError}. In particular,
 * {@link io.github.libxposed.api.error.HookFailedError} indicates a fatal hook failure that
 * should be reported to the framework maintainers instead of handled by the module.</p>
 *
 * @see io.github.libxposed.api.XposedInterface
 * @see io.github.libxposed.api.XposedModule
 * @see io.github.libxposed.api.XposedModuleInterface
 */
package io.github.libxposed.api;
