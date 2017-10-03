package me.kolek.hub.fix.server.data;

import java.util.Map;

public class FixServerBean {
    private long serverId;
    private String hostId;
    private boolean autoStart;
    private Map<Long, FixEngineBean> engines;

    public long getServerId() {
        return serverId;
    }

    public void setServerId(long serverId) {
        this.serverId = serverId;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public boolean isAutoStart() {
        return autoStart;
    }

    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }

    public Map<Long, FixEngineBean> getEngines() {
        return engines;
    }

    public void setEngines(Map<Long, FixEngineBean> engines) {
        this.engines = engines;
    }
}
