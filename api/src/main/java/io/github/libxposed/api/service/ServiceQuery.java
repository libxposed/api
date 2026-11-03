package io.github.libxposed.api.service;

public interface ServiceQuery {
    Builder builder(String name);

    interface Builder {
        Builder setProviderPackage(String packageName);

        ServiceQuery build();
    }
}
