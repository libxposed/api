package io.github.libxposed.api.service;

public interface ServiceSubscription extends AutoCloseable {
    boolean isClosed();
}
