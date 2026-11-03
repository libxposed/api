package io.github.libxposed.api.service;

import android.os.IBinder;

import java.util.concurrent.Executor;

public interface ServiceRegistry {
    ServiceRegistration register(String name, IBinder binder);

    ServiceSubscription subscribe(ServiceQuery query, Executor executor, ServiceObserver observer);
}
