package me.kolek.hub.fix.server.management;

import me.kolek.fix.engine.config.FixEngineConfiguration;
import me.kolek.fix.engine.config.FixSessionConfiguration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FixServerConfiguration {
    private final long serverId;
    private final boolean autoStart;
    private final Map<String, FixEngineConfiguration> enginesByEngineId;
    private final Map<String, FixEngineConfiguration> enginesByConnectionId;

    public FixServerConfiguration(long serverId, boolean autoStart, Map<Long, FixEngineConfiguration> engines) {
        this.serverId = serverId;
        this.autoStart = autoStart;
        Map<String, FixEngineConfiguration> enginesByEngineId = new HashMap<>();
        Map<String, FixEngineConfiguration> enginesByConnectionId = new HashMap<>();
        engines.forEach((engineId, engine) -> {
            enginesByEngineId.put(engineId.toString(), engine);
            engine.getSessions().forEach(session -> {
                enginesByConnectionId.put(session.getSessionId(), engine);
            });
        });
        this.enginesByEngineId = Collections.unmodifiableMap(enginesByEngineId);
        this.enginesByConnectionId = Collections.unmodifiableMap(enginesByConnectionId);
    }

    public long getServerId() {
        return serverId;
    }

    public boolean isAutoStart() {
        return autoStart;
    }

    public FixEngineConfiguration getEngine(long engineId) {
        return enginesByEngineId.get(Long.toString(engineId));
    }

    public FixSessionConfiguration getSession(long connectionId) {
        String sessionId = Long.toString(connectionId);
        FixEngineConfiguration engine = enginesByConnectionId.get(sessionId);
        return engine != null ? engine.getSession(sessionId) : null;
    }
}
