package me.kolek.hub.fix.server.data;

import java.util.Map;

public class FixEngineBean {
    private long engineId;
    private String acceptorHost;
    private Integer acceptorPort;
    private Map<Long, FixConnectionBean> connections;

    public long getEngineId() {
        return engineId;
    }

    public void setEngineId(long engineId) {
        this.engineId = engineId;
    }

    public String getAcceptorHost() {
        return acceptorHost;
    }

    public void setAcceptorHost(String acceptorHost) {
        this.acceptorHost = acceptorHost;
    }

    public Integer getAcceptorPort() {
        return acceptorPort;
    }

    public void setAcceptorPort(Integer acceptorPort) {
        this.acceptorPort = acceptorPort;
    }

    public Map<Long, FixConnectionBean> getConnections() {
        return connections;
    }

    public void setConnections(Map<Long, FixConnectionBean> connections) {
        this.connections = connections;
    }
}
