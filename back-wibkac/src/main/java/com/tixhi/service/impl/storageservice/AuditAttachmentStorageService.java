package com.tixhi.service.impl.storageservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.tixhi.service.StorageService;

/**
 * <p>
 * Implementation of a storage service for staff member applications
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@Service
@Qualifier("Attachment")
public class AuditAttachmentStorageService extends FileSystemStorageService implements StorageService {

    @Autowired
    public AuditAttachmentStorageService(AuditAttachmentStorageProperties properties) {
        super(properties);
    }

}
