package me.kolek.hub.fix.serialization.data;

import me.kolek.util.CollectionUtil;
import me.kolek.util.tuple.Tuple;
import me.kolek.util.tuple.Tuple2;

import java.util.*;
import java.util.stream.Collectors;

public class FixStructureBean {
    private long structureId;
    private String name;
    private char typeCd;
    private String msgType;
    private FixFieldBean noField;
    private Map<Tuple2<String, String>, List<FixStructureElementBean>> elements;

    public long getStructureId() {
        return structureId;
    }

    public void setStructureId(long structureId) {
        this.structureId = structureId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public char getTypeCd() {
        return typeCd;
    }

    public void setTypeCd(char typeCd) {
        this.typeCd = typeCd;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public FixFieldBean getNoField() {
        return noField;
    }

    public void setNoField(FixFieldBean noField) {
        this.noField = noField;
    }

    public List<FixStructureElementBean> getElements() {
        return elements.values().stream().flatMap(List::stream).collect(Collectors.toList());
    }

    public List<FixStructureElementBean> getElements(String fixVersion, String schemaCd) {
        List<FixStructureElementBean> elements = new ArrayList<>();
        elements.addAll(this.elements.getOrDefault(Tuple.of(fixVersion, (String) null), Collections.emptyList()));
        elements.addAll(this.elements.getOrDefault(Tuple.of(fixVersion, schemaCd), Collections.emptyList()));
        elements.sort(Comparator.comparingInt(FixStructureElementBean::getOrderNo));
        return elements;
    }

    public void addElement(FixStructureElementBean element) {
        List<FixStructureElementBean> elements = this.elements
                .computeIfAbsent(Tuple.of(element.getFixVersion(), element.getSchemaCd()), k -> new ArrayList<>());
        CollectionUtil.addSorted(elements, element, Comparator.comparingInt(FixStructureElementBean::getOrderNo));
    }

    public void setElements(List<FixStructureElementBean> elements) {
        this.elements = elements.stream().collect(Collectors.groupingBy(
                Tuple.factory(FixStructureElementBean::getFixVersion, FixStructureElementBean::getSchemaCd)));
    }
}
