package me.kolek.hub.jdo.inject;

import org.datanucleus.api.jdo.JDOPersistenceManagerFactory;
import org.datanucleus.metadata.PersistenceUnitMetaData;
import org.glassfish.hk2.api.Factory;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import java.util.Map;

public class PersistenceManagerFactory implements Factory<PersistenceManager> {
    private final javax.jdo.PersistenceManagerFactory pmf;

    public PersistenceManagerFactory(String persistenceUnitName, Map<?, ?> overrides) {
        this.pmf = JDOHelper.getPersistenceManagerFactory(overrides, persistenceUnitName);
    }

    @Override
    public PersistenceManager provide() {
        return pmf.getPersistenceManager();
    }

    @Override
    public void dispose(PersistenceManager instance) {
        instance.close();
    }
}
