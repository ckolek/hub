package me.kolek.hub.fix.server.management;

import me.kolek.hub.fix.server.cache.FixConfigurationCache;
import me.kolek.management.AbstractMBean;
import me.kolek.management.MBean;
import org.glassfish.hk2.api.ServiceLocator;

import javax.inject.Inject;
import javax.management.MalformedObjectNameException;
import java.util.HashMap;
import java.util.Map;

@MBean
public class FixServerManager extends AbstractMBean {
    private final ServiceLocator serviceLocator;
    private final FixConfigurationCache cache;

    private final Map<Long, FixServer> servers;

    @Inject
    protected FixServerManager(ServiceLocator serviceLocator, FixConfigurationCache cache)
            throws MalformedObjectNameException {
        super("me.kolek.hub");
        this.serviceLocator = serviceLocator;
        this.cache = cache;
        this.servers = new HashMap<>();
    }

    @Override
    protected void init() throws Exception {
        super.init();
        servers.clear();
        for (FixServerConfiguration configuration : cache.getLocalHostConfigurations()) {
            servers.put(configuration.getServerId(), FixServer.create(serviceLocator, configuration));
        }
    }
}
