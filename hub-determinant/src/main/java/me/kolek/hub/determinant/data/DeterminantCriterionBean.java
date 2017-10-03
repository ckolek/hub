package me.kolek.hub.determinant.data;

import java.util.Collection;

public class DeterminantCriterionBean {
    private long determId;
    private int orderNo;
    private DeterminantPropertyBean property;
    private boolean negate;
    private String operatorCd;
    private String valueTypeCd;
    private boolean shortCircuit;
    private Collection<DeterminantCriterionValueBean> values;

    public long getDetermId() {
        return determId;
    }

    public int getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }

    public DeterminantPropertyBean getProperty() {
        return property;
    }

    public void setProperty(DeterminantPropertyBean property) {
        this.property = property;
    }

    public boolean isNegated() {
        return negate;
    }

    public void setNegated(boolean negate) {
        this.negate = negate;
    }

    public String getOperatorCd() {
        return operatorCd;
    }

    public void setOperatorCd(String operatorCd) {
        this.operatorCd = operatorCd;
    }

    public String getValueTypeCd() {
        return valueTypeCd;
    }

    public void setValueTypeCd(String valueTypeCd) {
        this.valueTypeCd = valueTypeCd;
    }

    public boolean isShortCircuited() {
        return shortCircuit;
    }

    public void setShortCircuited(boolean shortCircuit) {
        this.shortCircuit = shortCircuit;
    }

    public Collection<DeterminantCriterionValueBean> getValues() {
        return values;
    }

    public void setValues(Collection<DeterminantCriterionValueBean> values) {
        this.values = values;
    }
}
