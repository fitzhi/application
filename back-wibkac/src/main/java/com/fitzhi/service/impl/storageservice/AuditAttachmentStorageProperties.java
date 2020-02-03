package com.fitzhi.service.impl.storageservice;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="storage.attachments")
public class AuditAttachmentStorageProperties extends StorageProperties {
}
