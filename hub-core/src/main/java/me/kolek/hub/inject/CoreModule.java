package me.kolek.hub.inject;

import me.kolek.hub.HubModule;
import me.kolek.hub.management.inject.ManagementBinder;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;

public class CoreModule extends HubModule {
    @Override
    public void bind(ServiceLocator serviceLocator) throws Exception {
        ServiceLocatorUtilities.bind(serviceLocator, new ManagementBinder());
    }
}
