package me.kolek.hub.determinant;

import me.kolek.hub.HubModule;
import me.kolek.hub.determinant.cache.DeterminantCache;
import me.kolek.hub.determinant.cache.DeterminantCacheImpl;
import me.kolek.hub.determinant.data.*;
import me.kolek.hub.inject.CoreModule;
import me.kolek.hub.jdo.inject.PersistenceManagerFactory;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;

import javax.inject.Singleton;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;

public class DeterminantModule extends HubModule {
    @Override
    public Set<Class<? extends HubModule>> getDependencies() {
        return Collections.singleton(CoreModule.class);
    }

    @Override
    public void bind(ServiceLocator serviceLocator) {
        ServiceLocatorUtilities.bind(serviceLocator, new Binder() {
            @Override
            protected void configure() {
                bindPersistenceManagerFactory("Determinant");
                bind(DeterminantCacheImpl.class).to(DeterminantCache.class).in(Singleton.class);
            }
        });
    }
}
