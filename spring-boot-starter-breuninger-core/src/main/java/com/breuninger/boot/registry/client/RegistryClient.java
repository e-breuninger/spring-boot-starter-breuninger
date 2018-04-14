package com.breuninger.boot.registry.client;

public interface RegistryClient {

  void registerService();

  boolean isRunning();
}
