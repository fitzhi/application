package com.fitzhi.service.impl.storageservice;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="storage.applications")
public class ApplicationStorageProperties extends StorageProperties {
}
