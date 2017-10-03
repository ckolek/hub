package me.kolek.hub.fix.serialization.data;

import java.util.Objects;
import java.util.stream.Stream;

public class FixFieldTypeSpecBean {
    private String fixVersion;
    private String schemaCd;
    private String format1;
    private String format2;
    private Double multiplier;

    public String getFixVersion() {
        return fixVersion;
    }

    public void setFixVersion(String fixVersion) {
        this.fixVersion = fixVersion;
    }

    public String getSchemaCd() {
        return schemaCd;
    }

    public void setSchemaCd(String schemaCd) {
        this.schemaCd = schemaCd;
    }

    public String getFormat1() {
        return format1;
    }

    public void setFormat1(String format1) {
        this.format1 = format1;
    }

    public String getFormat2() {
        return format2;
    }

    public void setFormat2(String format2) {
        this.format2 = format2;
    }

    public String[] getFormats() {
        return Stream.of(format1, format2).filter(Objects::nonNull).toArray(String[]::new);
    }

    public Double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(Double multiplier) {
        this.multiplier = multiplier;
    }
}
