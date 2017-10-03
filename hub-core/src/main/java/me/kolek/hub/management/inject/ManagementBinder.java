package me.kolek.hub.management.inject;

import me.kolek.hub.HubModule;
import me.kolek.hub.management.MBeanManager;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;

public class ManagementBinder extends HubModule.Binder {
    @Override
    protected void configure() {
        bind(ManagementFactory.getPlatformMBeanServer()).to(MBeanServer.class);
        bindMBean(MBeanManager.class);
    }
}
