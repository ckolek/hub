package me.kolek.hub.fix.serialization.data;

import me.kolek.util.tuple.Tuple;
import me.kolek.util.tuple.Tuple2;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FixFieldTypeBean {
    private String typeCd;
    private FixFieldTypeBean parent;
    private Map<Tuple2<String, String>, FixFieldTypeSpecBean> specs;

    public String getTypeCd() {
        return typeCd;
    }

    public void setTypeCd(String typeCd) {
        this.typeCd = typeCd;
    }

    public FixFieldTypeBean getParent() {
        return parent;
    }

    public void setParent(FixFieldTypeBean parent) {
        this.parent = parent;
    }

    public Collection<FixFieldTypeSpecBean> getSpecs() {
        return specs != null ? specs.values() : Collections.emptyList();
    }

    public FixFieldTypeSpecBean getSpec(String fixVersion, String schemaCd) {
        return specs != null ? specs.get(Tuple.of(fixVersion, schemaCd)) : null;
    }

    public void setSpecs(Collection<FixFieldTypeSpecBean> specs) {
        this.specs = specs.stream().collect(Collectors
                .toMap(Tuple.factory(FixFieldTypeSpecBean::getFixVersion, FixFieldTypeSpecBean::getSchemaCd),
                        Function.identity()));
    }
}
