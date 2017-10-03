package me.kolek.hub.fix.serialization;

import me.kolek.hub.HubModule;
import me.kolek.hub.fix.serialization.cache.FixSerializationCache;
import me.kolek.hub.fix.serialization.cache.FixSerializationCacheImpl;
import me.kolek.hub.inject.CoreModule;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;

import javax.inject.Singleton;
import java.util.Collections;
import java.util.Set;

public class FixSerializationModule extends HubModule {
    @Override
    public Set<Class<? extends HubModule>> getDependencies() {
        return Collections.singleton(CoreModule.class);
    }

    @Override
    public void bind(ServiceLocator serviceLocator) {
        ServiceLocatorUtilities.bind(serviceLocator, new Binder() {
            @Override
            protected void configure() {
                bind(FixSerializationCacheImpl.class).to(FixSerializationCache.class).in(Singleton.class);
            }
        });
    }
}
