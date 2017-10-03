package me.kolek.hub.fix.serialization.cache;

import me.kolek.fix.serialization.metadata.MessageMetadata;
import me.kolek.hub.cache.Cache;
import me.kolek.hub.cache.CacheException;
import me.kolek.hub.cache.DataUnavailableException;

public interface FixSerializationCache extends Cache {
    MessageMetadata getMetadata(String msgType, String fixVersion, String schema) throws CacheException;
}
