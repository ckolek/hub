package me.kolek.hub.fix.serialization.data;

public class FixStructureElementBean {
    private String fixVersion;
    private String schemaCd;
    private int orderNo;
    private FixStructureBean substructure;
    private FixFieldBean field;
    private boolean required;
    private boolean trailing;

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

    public int getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }

    public FixStructureBean getSubstructure() {
        return substructure;
    }

    public void setSubstructure(FixStructureBean substructure) {
        this.substructure = substructure;
    }

    public FixFieldBean getField() {
        return field;
    }

    public void setField(FixFieldBean field) {
        this.field = field;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isTrailing() {
        return trailing;
    }

    public void setTrailing(boolean trailing) {
        this.trailing = trailing;
    }
}
