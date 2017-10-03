package me.kolek.hub.cache;

public class NotFoundException extends CacheException {
    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(Throwable cause) {
        super(cause);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundException(Class<?> cachedType, String field, Object value) {
        this(cachedType.getSimpleName() + "{ " + field + ": " + value + " }");
    }
}
