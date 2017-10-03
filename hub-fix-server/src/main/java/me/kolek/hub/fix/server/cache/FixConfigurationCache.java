package me.kolek.hub.fix.server.cache;

import me.kolek.hub.cache.CacheException;
import me.kolek.hub.fix.server.management.FixServerConfiguration;

import java.util.Collection;
import java.util.List;

public interface FixConfigurationCache {
    FixServerConfiguration getByServerId(long serverId) throws CacheException;

    Collection<FixServerConfiguration> getByHostId(String hostId) throws CacheException;

    Collection<FixServerConfiguration> getLocalHostConfigurations() throws CacheException;
}
