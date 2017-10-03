package me.kolek.hub.util;

public enum HubUtil {
    ;
    private static final String HOST_ID = System.getProperty("me.kolek.hub.host_id", "unknown");
    private static final String MODE = System.getProperty("me.kolek.hub.mode", "none");

    public static String getHostId() {
        if (isUnitTestMode()) {
            return "testhub";
        }
        return HOST_ID;
    }

    public static boolean isUnitTestMode() {
        return "unittest".equalsIgnoreCase(MODE);
    }
}
