package me.kolek.hub;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;

import java.util.List;

public class Hub {
    private ServiceLocator serviceLocator;
    private List<HubModule> modules;

    public static void main(String[] args) throws Exception {
        Hub hub = new Hub();
        hub.launch();
    }

    public void launch() throws Exception {
        serviceLocator = createServiceLocator();
        modules = HubModule.discoverAndInitializeModules(serviceLocator);
    }

    public static ServiceLocator createServiceLocator() {
        ServiceLocator serviceLocator = ServiceLocatorUtilities.createAndPopulateServiceLocator("Hub");
        ServiceLocatorUtilities.enableImmediateScope(serviceLocator);
        return serviceLocator;
    }
}
