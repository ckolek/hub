package me.kolek.hub.fix.serialization.data;

public class FixFieldBean {
    private long fieldId;
    private String name;
    private int tagNum;
    private FixFieldTypeBean type;

    public long getFieldId() {
        return fieldId;
    }

    public void setFieldId(long fieldId) {
        this.fieldId = fieldId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTagNum() {
        return tagNum;
    }

    public void setTagNum(int tagNum) {
        this.tagNum = tagNum;
    }

    public FixFieldTypeBean getType() {
        return type;
    }

    public void setType(FixFieldTypeBean type) {
        this.type = type;
    }
}
