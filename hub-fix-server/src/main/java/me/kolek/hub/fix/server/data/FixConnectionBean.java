package me.kolek.hub.fix.server.data;

public class FixConnectionBean {
    private long connectionId;
    private String name;
    private boolean acceptor;
    private String fixVersion;
    private String transportVersion;
    private String senderCompId;
    private String senderSubId;
    private String senderLocationId;
    private String targetCompId;
    private String targetSubId;
    private String targetLocationId;
    private String host;
    private Integer port;
    private String fallbackHost1;
    private Integer fallbackPort1;
    private String fallbackHost2;
    private Integer fallbackPort2;
    private Integer heartbeatInterval;

    public long getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(long connectionId) {
        this.connectionId = connectionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAcceptor() {
        return acceptor;
    }

    public void setAcceptor(boolean acceptor) {
        this.acceptor = acceptor;
    }

    public String getFixVersion() {
        return fixVersion;
    }

    public void setFixVersion(String fixVersion) {
        this.fixVersion = fixVersion;
    }

    public String getTransportVersion() {
        return transportVersion;
    }

    public void setTransportVersion(String transportVersion) {
        this.transportVersion = transportVersion;
    }

    public String getSenderCompId() {
        return senderCompId;
    }

    public void setSenderCompId(String senderCompId) {
        this.senderCompId = senderCompId;
    }

    public String getSenderSubId() {
        return senderSubId;
    }

    public void setSenderSubId(String senderSubId) {
        this.senderSubId = senderSubId;
    }

    public String getSenderLocationId() {
        return senderLocationId;
    }

    public void setSenderLocationId(String senderLocationId) {
        this.senderLocationId = senderLocationId;
    }

    public String getTargetCompId() {
        return targetCompId;
    }

    public void setTargetCompId(String targetCompId) {
        this.targetCompId = targetCompId;
    }

    public String getTargetSubId() {
        return targetSubId;
    }

    public void setTargetSubId(String targetSubId) {
        this.targetSubId = targetSubId;
    }

    public String getTargetLocationId() {
        return targetLocationId;
    }

    public void setTargetLocationId(String targetLocationId) {
        this.targetLocationId = targetLocationId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getFallbackHost1() {
        return fallbackHost1;
    }

    public void setFallbackHost1(String fallbackHost1) {
        this.fallbackHost1 = fallbackHost1;
    }

    public Integer getFallbackPort1() {
        return fallbackPort1;
    }

    public void setFallbackPort1(Integer fallbackPort1) {
        this.fallbackPort1 = fallbackPort1;
    }

    public String getFallbackHost2() {
        return fallbackHost2;
    }

    public void setFallbackHost2(String fallbackHost2) {
        this.fallbackHost2 = fallbackHost2;
    }

    public Integer getFallbackPort2() {
        return fallbackPort2;
    }

    public void setFallbackPort2(Integer fallbackPort2) {
        this.fallbackPort2 = fallbackPort2;
    }

    public Integer getHeartbeatInterval() {
        return heartbeatInterval;
    }

    public void setHeartbeatInterval(Integer heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
    }
}
