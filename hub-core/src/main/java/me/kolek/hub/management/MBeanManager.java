package me.kolek.hub.management;

import me.kolek.management.AbstractMBean;
import me.kolek.management.MBean;

import javax.inject.Inject;
import javax.management.*;

import static me.kolek.management.MBeanUtil.objectName;

@MBean
public class MBeanManager extends AbstractMBean {
    private final MBeanServer server;

    @Inject
    protected MBeanManager(MBeanServer server) throws MalformedObjectNameException {
        super(objectName(HubMBean.DOMAIN).type(MBeanManager.class).build());
        this.server = server;
    }

    @Override
    protected void init() throws Exception {
        super.init();
        register(this);
    }

    @Override
    protected void destroy() throws Exception {
        unregister(this);
        super.destroy();
    }

    public void register(AbstractMBean mBean) throws JMException {
        server.registerMBean(mBean, mBean.getObjectName());
    }

    public void unregister(AbstractMBean mBean) throws JMException {
        server.unregisterMBean(mBean.getObjectName());
    }
}
