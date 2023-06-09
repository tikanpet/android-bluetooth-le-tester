package com.example.ble_test_v13_0;

public class ServiceModel {
    private final String serviceName;
    private final String serviceUUID;

    public ServiceModel(String serviceName, String serviceUUID) {
        this.serviceName = serviceName;
        this.serviceUUID = serviceUUID;
    }

    public String getServiceName() {
        return this.serviceName;
    }

    public String getServiceUUID() {
        return this.serviceUUID;
    }
}
