-keep class io.github.libxposed.** { *; }
-keepclassmembers,allowoptimization class ** implements io.github.libxposed.api.XposedInterface$MethodHooker {
    java.lang.Object intercept(io.github.libxposed.api.XposedInterface$MethodChain);
}
-keepclassmembers,allowoptimization class ** implements io.github.libxposed.api.XposedInterface$VoidMethodHooker {
    void intercept(io.github.libxposed.api.XposedInterface$MethodChain);
}
-keepclassmembers,allowoptimization class ** implements io.github.libxposed.api.XposedInterface$CtorHooker {
    void intercept(io.github.libxposed.api.XposedInterface$CtorChain);
}
