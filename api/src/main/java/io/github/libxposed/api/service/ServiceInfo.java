package io.github.libxposed.api.service;

import android.os.IBinder;

public interface ServiceInfo {
    String getName();

    String getProviderPackageName();

    String getProcessName();

    long getInstanceId();

    IBinder getBinder();
}
