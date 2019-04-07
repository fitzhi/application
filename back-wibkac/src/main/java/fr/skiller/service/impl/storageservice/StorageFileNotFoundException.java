package fr.skiller.service.impl.storageservice;

public class StorageFileNotFoundException extends StorageException {

    /**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -1865185765515701210L;

	public StorageFileNotFoundException(String message) {
        super(message);
    }

    public StorageFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}