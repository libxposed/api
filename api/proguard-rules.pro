-keep class io.github.libxposed.** { *; }
-keepclassmembers,allowoptimization class ** implements io.github.libxposed.api.XposedInterface$Hooker {
    public *** before(***);
    public *** after(***);
}
