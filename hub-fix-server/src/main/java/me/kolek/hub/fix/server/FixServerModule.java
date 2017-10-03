package me.kolek.hub.fix.server;

import me.kolek.hub.HubModule;
import me.kolek.hub.determinant.DeterminantModule;
import me.kolek.hub.fix.serialization.FixSerializationModule;
import me.kolek.hub.fix.server.management.FixServerManager;
import me.kolek.hub.inject.CoreModule;
import me.kolek.util.CollectionUtil;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;

import java.util.Set;

public class FixServerModule extends HubModule {
    @Override
    public Set<Class<? extends HubModule>> getDependencies() {
        return CollectionUtil.toSet(CoreModule.class, DeterminantModule.class, FixSerializationModule.class);
    }

    @Override
    public void bind(ServiceLocator serviceLocator) {
        ServiceLocatorUtilities.bind(serviceLocator, new Binder() {
            @Override
            protected void configure() {
                bindPersistenceManagerFactory("FixServer");
                bindMBean(FixServerManager.class);
            }
        });
    }
}
