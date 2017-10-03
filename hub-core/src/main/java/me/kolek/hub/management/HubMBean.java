package me.kolek.hub.management;

import me.kolek.management.AbstractMBean;
import me.kolek.management.MBeanUtil;
import me.kolek.management.ObjectNameBuilder;

import javax.inject.Inject;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

public abstract class HubMBean extends AbstractMBean {
    static final String DOMAIN = "me.kolek.hub";

    @Inject
    private MBeanManager manager;

    protected HubMBean() throws MalformedObjectNameException {
        super(DOMAIN);
    }

    protected HubMBean(ObjectName objectName) {
        super(objectName);
    }

    @Override
    protected void init() throws Exception {
        super.init();
        manager.register(this);
    }

    @Override
    protected void destroy() throws Exception {
        manager.unregister(this);
        super.destroy();
    }

    protected static ObjectNameBuilder objectName() {
        return MBeanUtil.objectName(DOMAIN);
    }
}
