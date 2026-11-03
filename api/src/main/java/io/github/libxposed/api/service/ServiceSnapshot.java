package io.github.libxposed.api.service;

import java.util.List;

public interface ServiceSnapshot {
    long getRevision();

    List<ServiceInfo> getServices();
}
