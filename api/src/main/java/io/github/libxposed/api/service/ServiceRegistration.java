package io.github.libxposed.api.service;

public interface ServiceRegistration extends AutoCloseable {
    boolean isClosed();
}
