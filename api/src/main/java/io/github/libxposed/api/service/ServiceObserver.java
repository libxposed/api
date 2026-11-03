package io.github.libxposed.api.service;

public interface ServiceObserver {
    void onServicesChanged(ServiceSnapshot snapshot);
}
