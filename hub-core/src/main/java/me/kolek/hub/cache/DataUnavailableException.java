package me.kolek.hub.cache;

public class DataUnavailableException extends CacheException {
    public DataUnavailableException(String message) {
        super(message);
    }

    public DataUnavailableException(Throwable cause) {
        super(cause);
    }

    public DataUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
