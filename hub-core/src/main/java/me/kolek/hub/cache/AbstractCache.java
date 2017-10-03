package me.kolek.hub.cache;

import me.kolek.hub.management.HubMBean;
import me.kolek.management.MBean;
import me.kolek.management.MBeanAttribute;
import me.kolek.management.MBeanOperation;

import javax.management.MalformedObjectNameException;
import java.util.concurrent.atomic.AtomicInteger;

@MBean
public abstract class AbstractCache extends HubMBean implements Cache {
    private final AtomicInteger flushCount;

    public AbstractCache(String name) throws MalformedObjectNameException {
        super(objectName().type("cache").property("name", name).build());
        this.flushCount = new AtomicInteger(1);
    }

    @MBeanOperation
    public void flush() {
        flushCount.incrementAndGet();
    }

    @MBeanAttribute
    public boolean needsLoad() {
        return flushCount.get() > 0;
    }

    protected void load() throws DataUnavailableException {
        if (flushCount.get() <= 0) {
            return;
        }

        try {
            _load();
            flushCount.set(0);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new DataUnavailableException(e);
        }
    }

    protected abstract void _load() throws DataUnavailableException;
}
