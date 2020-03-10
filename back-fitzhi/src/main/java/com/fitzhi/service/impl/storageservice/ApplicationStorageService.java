package com.fitzhi.service.impl.storageservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fitzhi.service.StorageService;

/**
 * <p>
 * Implementation of a storage service for staff member applications
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
@Service
@Qualifier("Application")
public class ApplicationStorageService extends FileSystemStorageService implements StorageService {

    @Autowired
    public ApplicationStorageService(ApplicationStorageProperties properties) {
        super(properties);
    }

}
