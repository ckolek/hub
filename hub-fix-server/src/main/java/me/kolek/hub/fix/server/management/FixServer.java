package me.kolek.hub.fix.server.management;

import me.kolek.management.AbstractMBean;
import me.kolek.management.MBean;
import me.kolek.management.MBeanOperation;
import me.kolek.management.MBeanUtil;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;

import javax.management.MalformedObjectNameException;

@MBean
public class FixServer extends AbstractMBean {
    private final FixServerConfiguration configuration;

    private FixServer(FixServerConfiguration configuration) throws MalformedObjectNameException {
        super(MBeanUtil.objectName("me.kolek.hub").type(FixServer.class).property("id", configuration.getServerId())
                .build());
        this.configuration = configuration;
    }

    @Override
    protected void init() throws Exception {
        super.init();
        if (configuration.isAutoStart()) {
            start();
        }
    }

    @MBeanOperation
    public void start() {

    }

    @MBeanOperation
    public void shutdown() {

    }

    public static FixServer create(ServiceLocator serviceLocator, FixServerConfiguration configuration)
            throws Exception {
        FixServer fixServer = new FixServer(configuration);
        serviceLocator.inject(fixServer);
        serviceLocator.postConstruct(fixServer);
        return fixServer;
    }
}
