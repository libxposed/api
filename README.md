# libxposed API

[![API](https://img.shields.io/badge/API-101-brightgreen)](https://github.com/libxposed/api)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.libxposed/api?color=blue)](https://central.sonatype.com/artifact/io.github.libxposed/api)
[![Android Min SDK](https://img.shields.io/badge/minSdk-26-orange)](https://developer.android.com/about/versions/oreo)
[![License](https://img.shields.io/github/license/libxposed/api)](LICENSE)

Modern Xposed Module API — a type-safe, redesigned replacement for the legacy XposedBridge API.

## Integration

### For Module Developers

```kotlin
dependencies {
    compileOnly("io.github.libxposed:api:101.0.1")
}
```

### For Framework Developers

```kotlin
dependencies {
    implementation("io.github.libxposed:api:101.0.1")
}
```

## Documentation

- [Guide](https://github.com/LSPosed/LSPosed/wiki/Develop-Xposed-Modules-Using-Modern-Xposed-API) — Getting started with the modern Xposed API
- [Javadoc](https://libxposed.github.io/api/) — API reference

## Related Projects

- [libxposed/helper](https://github.com/libxposed/helper) — Friendly development kit library
- [libxposed/service](https://github.com/libxposed/service) — Framework communication service
- [libxposed/example](https://github.com/libxposed/example) - Example module using the modern Xposed API

## License

This project is licensed under the [Apache License 2.0](LICENSE).
