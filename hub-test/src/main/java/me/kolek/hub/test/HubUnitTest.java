package me.kolek.hub.test;

import me.kolek.hub.Hub;
import me.kolek.hub.HubModule;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.junit.After;
import org.junit.Before;

import java.util.Collections;
import java.util.List;

public class HubUnitTest {
    private final Class<? extends HubModule> moduleClass;

    private ServiceLocator serviceLocator;
    private List<HubModule> modules;

    public HubUnitTest(Class<? extends HubModule> moduleClass) {
        this.moduleClass = moduleClass;
    }

    @Before
    public void setUp() throws Exception {
        serviceLocator = Hub.createServiceLocator();
        modules = HubModule.createModules(Collections.singleton(moduleClass));
        HubModule.initializeModules(serviceLocator, modules);
    }

    public ServiceLocator getServiceLocator() {
        if (serviceLocator == null) {
            throw new IllegalStateException("must call setUp before using ServiceLocator");
        }
        return serviceLocator;
    }

    @After
    public void tearDown() throws Exception {
        HubModule.destroyModules(serviceLocator, modules);
        serviceLocator.shutdown();
    }
}
