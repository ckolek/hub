package me.kolek.hub.determinant.data;

import java.util.List;

public class DeterminantCriterionUnionBean {
    private long determId;
    private int orderNo;
    private DeterminantCriterionUnionBean parent;
    private List<DeterminantCriterionUnionBean> children;
    private boolean negate;
    private boolean shortCircuit;
    private List<DeterminantCriterionBean> criteria;

    public long getDetermId() {
        return determId;
    }

    public int getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }

    public DeterminantCriterionUnionBean getParent() {
        return parent;
    }

    public void setParent(DeterminantCriterionUnionBean parent) {
        this.parent = parent;
    }

    public List<DeterminantCriterionUnionBean> getChildren() {
        return children;
    }

    public void setChildren(List<DeterminantCriterionUnionBean> children) {
        this.children = children;
    }

    public boolean isNegated() {
        return negate;
    }

    public void setNegated(boolean negate) {
        this.negate = negate;
    }

    public boolean isShortCircuited() {
        return shortCircuit;
    }

    public void setShortCircuited(boolean shortCircuit) {
        this.shortCircuit = shortCircuit;
    }

    public List<DeterminantCriterionBean> getCriteria() {
        return criteria;
    }

    public void setCriteria(List<DeterminantCriterionBean> criteria) {
        this.criteria = criteria;
    }
}
