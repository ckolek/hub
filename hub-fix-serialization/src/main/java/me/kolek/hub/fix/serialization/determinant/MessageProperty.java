package me.kolek.hub.fix.serialization.determinant;

import me.kolek.determinant.DeterminantProperty;
import me.kolek.determinant.UnknownPropertyException;
import me.kolek.fix.serialization.Component;
import me.kolek.fix.serialization.Group;
import me.kolek.fix.serialization.Message;
import me.kolek.fix.serialization.Structure;
import me.kolek.fix.serialization.metadata.FieldValue;
import me.kolek.fix.serialization.metadata.StructureMember;
import me.kolek.fix.serialization.metadata.StructureMetadata;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class MessageProperty {
    private static final Pattern GROUP_ELEMENT_PATTERN = Pattern.compile("(\\d+)\\[(\\d+|\\*)]");

    private final List<SubstructureAccessor> substructureAccessors;
    private final FieldAccessor fieldAccessor;

    MessageProperty(List<SubstructureAccessor> substructureAccessors, FieldAccessor fieldAccessor) {
        this.substructureAccessors = Collections.unmodifiableList(substructureAccessors);
        this.fieldAccessor = fieldAccessor;
    }

    Object getValue(Message message) throws UnknownPropertyException {
        List<Structure> structures = Collections.singletonList(message);
        for (SubstructureAccessor accessor : substructureAccessors) {
            List<Structure> substructures = new ArrayList<>();
            for (Structure structure : structures) {
                substructures.addAll(accessor.access(structure));
            }
            if (substructures.isEmpty()) {
                return null;
            }
            structures = substructures;
        }

        Set<Object> values = new HashSet<>();
        for (Structure structure : structures) {
            values.add(fieldAccessor.access(structure));
        }
        switch (values.size()) {
            case 0:
                return null;
            case 1:
                return values.iterator().next();
            default:
                return values;
        }
    }

    static MessageProperty compile(DeterminantProperty property) throws UnknownPropertyException {
        String[] parts = property.getName().split("\\.");
        List<SubstructureAccessor> substructureAccessors = new ArrayList<>();
        FieldAccessor fieldAccessor = null;
        for (Iterator<String> iter = Arrays.asList(parts).iterator(); iter.hasNext(); ) {
            String element = iter.next();
            if (!iter.hasNext()) {
                int tagNum;
                try {
                    tagNum = Integer.parseInt(element);
                } catch (NumberFormatException e) {
                    throw new UnknownPropertyException(property);
                }
                fieldAccessor = new FieldAccessor(property, tagNum);
            } else {
                Matcher matcher = GROUP_ELEMENT_PATTERN.matcher(element);
                if (matcher.matches()) {
                    int tagNum = Integer.parseInt(matcher.group(1));
                    String _index = matcher.group(2);
                    int index = "*".equals(_index) ? -1 : Integer.parseInt(_index);
                    substructureAccessors.add(new SubGroupAccessor(property, tagNum, index));
                } else {
                    substructureAccessors.add(new SubComponentAccessor(property, element));
                }
            }
        }
        return new MessageProperty(substructureAccessors, fieldAccessor);
    }

    private static abstract class SubstructureAccessor {
        protected final DeterminantProperty property;

        protected SubstructureAccessor(DeterminantProperty property) {
            this.property = property;
        }

        protected abstract List<? extends Structure> access(Structure structure) throws UnknownPropertyException;
    }

    private static class SubComponentAccessor extends SubstructureAccessor {
        private final String name;

        private SubComponentAccessor(DeterminantProperty property, String name) {
            super(property);
            this.name = name;
        }

        @Override
        protected List<? extends Structure> access(Structure structure) throws UnknownPropertyException {
            Component component = structure.getComponent(name);
            if (component != null) {
                return Collections.singletonList(component);
            } else {
                StructureMetadata metadata = structure.getMetadata();
                if (metadata.hasComponentMember(name)) {
                    return Collections.emptyList();
                } else {
                    throw new UnknownPropertyException(property);
                }
            }
        }
    }

    private static class SubGroupAccessor extends SubstructureAccessor {
        private final int tagNum;
        private final int index;

        private SubGroupAccessor(DeterminantProperty property, int tagNum, int index) {
            super(property);
            this.tagNum = tagNum;
            this.index = index;
        }

        @Override
        protected List<? extends Structure> access(Structure structure) throws UnknownPropertyException {
            Group group = structure.getGroup(tagNum);
            if (group != null) {
                if (index >= 0) {
                    if (index < group.size()) {
                        return Collections.singletonList(group.get(index));
                    } else {
                        return Collections.emptyList();
                    }
                } else {
                    return group.elements();
                }
            } else {
                StructureMetadata metadata = structure.getMetadata();
                if (metadata.hasGroupMember(tagNum)) {
                    return Collections.emptyList();
                } else {
                    throw new UnknownPropertyException(property);
                }
            }
        }
    }

    private static class FieldAccessor {
        private final DeterminantProperty property;
        private final int tagNum;

        private FieldAccessor(DeterminantProperty property, int tagNum) {
            this.property = property;
            this.tagNum = tagNum;
        }

        Object access(Structure structure) throws UnknownPropertyException {
            FieldValue<?> fieldValue = structure.get(tagNum);
            if (fieldValue != null) {
                return fieldValue.getValue();
            } else {
                StructureMetadata metadata = structure.getMetadata();
                if (metadata.hasFieldMember(tagNum) || metadata.hasGroupMember(tagNum) ||
                        metadata.hasComponentContaining(tagNum)) {
                    return null;
                } else {
                    throw new UnknownPropertyException(property);
                }
            }
        }
    }
}
