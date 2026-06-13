# These rules are for the framework. Module rules should refer to README.md.
-dontwarn io.github.libxposed.annotation.**
-keep,allowoptimization public class io.github.libxposed.api.** {
    public <fields>;
    protected <fields>;
    public <methods>;
    protected <methods>;
    public <init>(...);
    protected <init>(...);
}
