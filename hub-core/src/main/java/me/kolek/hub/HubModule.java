package me.kolek.hub;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import me.kolek.hub.jdo.inject.PersistenceManagerFactory;
import me.kolek.hub.management.HubMBean;
import me.kolek.management.AbstractMBean;
import me.kolek.util.CollectionUtil;
import me.kolek.util.structs.Result;
import org.glassfish.hk2.api.Immediate;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;
import java.util.stream.Collectors;

public abstract class HubModule {
    public Set<Class<? extends HubModule>> getDependencies() {
        return Collections.emptySet();
    }

    public abstract void bind(ServiceLocator serviceLocator) throws Exception;

    public static List<HubModule> discoverAndInitializeModules(ServiceLocator serviceLocator) throws Exception {
        Collection<Class<? extends HubModule>> moduleClasses =
                discoverModuleClasses(Thread.currentThread().getContextClassLoader());
        List<HubModule> modules = createModules(moduleClasses);
        initializeModules(serviceLocator, modules);
        return modules;
    }

    public static Collection<Class<? extends HubModule>> discoverModuleClasses(ClassLoader classLoader)
            throws Exception {
        Set<Class<? extends HubModule>> moduleClasses = new HashSet<>();
        Enumeration<URL> resources = classLoader.getResources("me/kolek/hub/module.fqcn");
        while (resources.hasMoreElements()) {
            String className = Resources.asByteSource(resources.nextElement()).asCharSource(Charset.defaultCharset())
                    .readFirstLine();
            Result<Class<? extends HubModule>, Exception> result = AccessController
                    .doPrivileged((PrivilegedAction<Result<Class<? extends HubModule>, Exception>>) () -> {
                        try {
                            Class<?> clazz = Class.forName(className);
                            if (!HubModule.class.isAssignableFrom(clazz)) {
                                return Result
                                        .failure(new Exception("class " + className + " does not extend HubModule"));
                            }
                            return Result.success((Class<? extends HubModule>) clazz);
                        } catch (ClassNotFoundException e) {
                            return Result.failure(e);
                        }
                    });
            if (result.isFailed()) {
                throw result.getError();
            }
            moduleClasses.add(result.getValue());
        }
        return moduleClasses;
    }

    public static List<HubModule> createModules(Collection<Class<? extends HubModule>> moduleClasses) throws Exception {
        Map<Class<? extends HubModule>, HubModule> moduleInstances = new HashMap<>();

        for (Class<? extends HubModule> moduleClass : moduleClasses) {
            createModules(moduleClass, moduleInstances);
        }

        List<Class<? extends HubModule>> sorted =
                CollectionUtil.topologicalSort(moduleClasses, mc -> moduleInstances.get(mc).getDependencies());

        return sorted.stream().map(moduleInstances::get).collect(Collectors.toList());
    }

    private static void createModules(Class<? extends HubModule> moduleClass,
            Map<Class<? extends HubModule>, HubModule> moduleInstances) throws Exception {
        if (!moduleInstances.containsKey(moduleClass)) {
            HubModule module = moduleClass.newInstance();
            moduleInstances.put(moduleClass, module);
            for (Class<? extends HubModule> dependency : module.getDependencies()) {
                createModules(dependency, moduleInstances);
            }
        }
    }

    public static void initializeModules(ServiceLocator serviceLocator, List<HubModule> modules) throws Exception {
        for (HubModule module : modules) {
            serviceLocator.inject(module);
            module.bind(serviceLocator);
            serviceLocator.postConstruct(module);
        }
    }

    public static void destroyModules(ServiceLocator serviceLocator, List<HubModule> modules) {
        for (HubModule module : modules) {
            serviceLocator.preDestroy(module);
        }
    }

    public static abstract class Binder extends AbstractBinder implements org.glassfish.hk2.utilities.Binder {
        protected void bindPersistenceManagerFactory(String persistenceUnitName) {
            Properties properties = new Properties();
            properties.put(JDOHelper.PROPERTY_CONNECTION_DRIVER_NAME, System.getProperty("me.kolek.hub.db.driver"));
            properties.put(JDOHelper.PROPERTY_CONNECTION_URL, System.getProperty("me.kolek.hub.db.url"));
            properties.put(JDOHelper.PROPERTY_CONNECTION_USER_NAME, System.getProperty("me.kolek.hub.db.user"));
            properties.put(JDOHelper.PROPERTY_CONNECTION_PASSWORD, System.getProperty("me.kolek.hub.db.password"));

            bindFactory(new PersistenceManagerFactory(persistenceUnitName, properties)).to(PersistenceManager.class)
                    .named(persistenceUnitName);
        }

        protected void bindMBean(Class<? extends AbstractMBean> mBeanClass) {
            bindAsContract(mBeanClass).in(Immediate.class);
        }
    }
}
