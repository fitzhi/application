package com.fitzhi.service.impl.storageservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

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
public class ApplicationStorageService extends FileSystemStorageService  {

    @Autowired
    public ApplicationStorageService(ApplicationStorageProperties properties) {
        super(properties);
    }

}
