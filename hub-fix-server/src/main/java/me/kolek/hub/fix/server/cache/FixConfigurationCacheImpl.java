package me.kolek.hub.fix.server.cache;

import me.kolek.fix.engine.config.FixEngineConfiguration;
import me.kolek.fix.engine.config.FixSessionConfiguration;
import me.kolek.fix.util.FixUtil;
import me.kolek.hub.cache.AbstractCache;
import me.kolek.hub.cache.CacheException;
import me.kolek.hub.cache.DataUnavailableException;
import me.kolek.hub.cache.NotFoundException;
import me.kolek.hub.fix.server.data.FixConnectionBean;
import me.kolek.hub.fix.server.data.FixEngineBean;
import me.kolek.hub.fix.server.data.FixServerBean;
import me.kolek.hub.fix.server.management.FixServerConfiguration;
import me.kolek.hub.util.HubUtil;

import javax.inject.Inject;
import javax.inject.Named;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.management.MalformedObjectNameException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class FixConfigurationCacheImpl extends AbstractCache implements FixConfigurationCache {
    private final PersistenceManager pm;

    private final AtomicReference<Data> reference;

    @Inject
    public FixConfigurationCacheImpl(@Named("FixServer") PersistenceManager pm) throws MalformedObjectNameException {
        super("FixConfiguration");
        this.pm = pm;
        this.reference = new AtomicReference<>();
    }

    @Override
    public FixServerConfiguration getByServerId(long serverId) throws CacheException {
        if (needsLoad()) {
            load();
        }
        return getData().map(d -> d.byServerId.get(serverId))
                .orElseThrow(() -> new NotFoundException(FixServerConfiguration.class, "serverId", serverId));
    }

    @Override
    public Collection<FixServerConfiguration> getByHostId(String hostId) throws CacheException {
        if (needsLoad()) {
            load();
        }
        return getData().map(d -> d.byHostId.get(hostId)).orElseGet(Collections::emptyList);
    }

    @Override
    public Collection<FixServerConfiguration> getLocalHostConfigurations() throws CacheException {
        return getByHostId(HubUtil.getHostId());
    }

    @Override
    protected void _load() throws DataUnavailableException {
        Map<Long, FixServerConfiguration> byServerId = new HashMap<>();
        Map<String, List<FixServerConfiguration>> byHostId = new HashMap<>();

        try (Query<FixServerBean> q = pm.newQuery(FixServerBean.class)) {
            for (FixServerBean serverBean : q.executeList()) {
                FixServerConfiguration configuration = toFixServerConfiguration(serverBean);
                byServerId.put(configuration.getServerId(), configuration);
                byHostId.computeIfAbsent(serverBean.getHostId(), k -> new ArrayList<>()).add(configuration);
            }
        } catch (Exception e) {
            throw new DataUnavailableException(e);
        }

        byHostId.replaceAll((hostId, configurations) -> Collections.unmodifiableList(configurations));

        reference.set(new Data(byServerId, byHostId));
    }

    private Optional<Data> getData() {
        return Optional.ofNullable(reference.get());
    }

    private static FixServerConfiguration toFixServerConfiguration(FixServerBean serverBean) {
        Map<Long, FixEngineConfiguration> engines = new HashMap<>();
        serverBean.getEngines().forEach((engineId, engineBean) -> {
            engines.put(engineId, toFixEngineConfiguration(engineBean));
        });
        return new FixServerConfiguration(serverBean.getServerId(), serverBean.isAutoStart(), engines);
    }

    private static FixEngineConfiguration toFixEngineConfiguration(FixEngineBean engineBean) {
        return FixEngineConfiguration.build(engine -> {
            engine.acceptorHost(engineBean.getAcceptorHost()).acceptorPort(engineBean.getAcceptorPort());
            engineBean.getConnections().values().stream().map(FixConfigurationCacheImpl::toFixSessionConfiguration)
                    .forEach(engine::session);
        });
    }

    private static FixSessionConfiguration toFixSessionConfiguration(FixConnectionBean connectionBean) {
        return FixSessionConfiguration.build(session -> {
            if (connectionBean.isAcceptor()) {
                session.acceptor();
            } else {
                session.initiator();
            }
            session.sessionId(Long.toString(connectionBean.getConnectionId()));
            if (connectionBean.getTransportVersion() != null) {
                session.beginString("FIXT." + connectionBean.getTransportVersion())
                        .defaultApplVerId(FixUtil.toApplVerId(connectionBean.getFixVersion()));
            } else {
                session.beginString(connectionBean.getFixVersion());
            }
            session.senderCompId(connectionBean.getSenderCompId()).senderSubId(connectionBean.getSenderSubId())
                    .senderLocationId(connectionBean.getSenderLocationId())
                    .targetCompId(connectionBean.getTargetCompId()).targetSubId(connectionBean.getTargetSubId())
                    .senderLocationId(connectionBean.getSenderLocationId());
            if (connectionBean.getHost() != null) {
                session.address(connectionBean.getHost(), connectionBean.getPort());
            }
            if (connectionBean.getFallbackHost1() != null) {
                session.address(connectionBean.getFallbackHost1(), connectionBean.getFallbackPort1());
            }
            if (connectionBean.getFallbackHost2() != null) {
                session.address(connectionBean.getFallbackHost2(), connectionBean.getFallbackPort2());
            }
            session.heartbeatInterval(connectionBean.getHeartbeatInterval());
        });
    }

    private static class Data {
        private final Map<Long, FixServerConfiguration> byServerId;
        private final Map<String, List<FixServerConfiguration>> byHostId;

        private Data(Map<Long, FixServerConfiguration> byServerId, Map<String, List<FixServerConfiguration>> byHostId) {
            this.byServerId = Collections.unmodifiableMap(byServerId);
            this.byHostId = Collections.unmodifiableMap(byHostId);
        }
    }
}
