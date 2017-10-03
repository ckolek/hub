package me.kolek.hub.determinant.data;

import java.util.Collection;
import java.util.List;

public class DeterminantBean {
    private long determId;
    private DeterminantTypeBean type;
    private DeterminantBean parent;
    private Collection<DeterminantBean> children;
    private String name;
    private String description;
    private double priority;
    private boolean active;
    private List<DeterminantCriterionUnionBean> unions;
    private List<DeterminantCriterionBean> criteria;

    public long getDetermId() {
        return determId;
    }

    public void setDetermId(long determId) {
        this.determId = determId;
    }

    public DeterminantTypeBean getType() {
        return type;
    }

    public void setType(DeterminantTypeBean type) {
        this.type = type;
    }

    public DeterminantBean getParent() {
        return parent;
    }

    public void setParent(DeterminantBean parent) {
        this.parent = parent;
    }

    public Collection<DeterminantBean> getChildren() {
        return children;
    }

    public void setChildren(Collection<DeterminantBean> children) {
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPriority() {
        return priority;
    }

    public void setPriority(double priority) {
        this.priority = priority;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<DeterminantCriterionUnionBean> getUnions() {
        return unions;
    }

    public void setUnions(List<DeterminantCriterionUnionBean> unions) {
        this.unions = unions;
    }

    public List<DeterminantCriterionBean> getCriteria() {
        return criteria;
    }

    public void setCriteria(List<DeterminantCriterionBean> criteria) {
        this.criteria = criteria;
    }
}
