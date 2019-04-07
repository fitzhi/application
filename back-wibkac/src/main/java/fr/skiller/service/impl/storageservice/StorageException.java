package fr.skiller.service.impl.storageservice;

public class StorageException extends RuntimeException {

    /**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 3360351112446432287L;

	public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
