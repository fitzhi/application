package fr.skiller.service.impl.storageservice;

public abstract class StorageProperties {

    /**
     * Folder location for storing files
     */
    public String location = "upload-dir";

    /**
     * @return the location for the uploading directory.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Set the location for the uploading operation.
     * @param location new location of upload
     */
    public void setLocation(String location) {
        this.location = location;
    }

}
