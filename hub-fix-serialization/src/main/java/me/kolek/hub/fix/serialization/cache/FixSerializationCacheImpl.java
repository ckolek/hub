package me.kolek.hub.fix.serialization.cache;

import me.kolek.fix.constants.Field;
import me.kolek.fix.serialization.field.FieldSerDes;
import me.kolek.fix.serialization.metadata.*;
import me.kolek.hub.cache.AbstractCache;
import me.kolek.hub.cache.CacheException;
import me.kolek.hub.cache.DataUnavailableException;
import me.kolek.hub.cache.NotFoundException;
import me.kolek.hub.fix.serialization.data.*;
import me.kolek.util.tuple.Tuple;
import me.kolek.util.tuple.Tuple3;

import javax.inject.Inject;
import javax.inject.Named;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.management.MalformedObjectNameException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class FixSerializationCacheImpl extends AbstractCache implements FixSerializationCache {
    private final PersistenceManager pm;

    private final AtomicReference<Data> reference;

    @Inject
    public FixSerializationCacheImpl(@Named("FixServer") PersistenceManager pm) throws MalformedObjectNameException {
        super("FixSerialization");
        this.pm = pm;
        this.reference = new AtomicReference<>(new Data());
    }

    @Override
    public MessageMetadata getMetadata(String msgType, String fixVersion, String schema) throws CacheException {
        if (needsLoad()) {
            load();
        }

        return reference.get().getMetadata(msgType, fixVersion, schema);
    }

    @Override
    protected void _load() throws DataUnavailableException {
        this.reference.set(new Data());
    }

    private class Data {
        private final Map<Tuple3<String, String, String>, MessageMetadata> messages;
        private final Map<Tuple3<Long, String, String>, ComponentMetadata> components;
        private final Map<Tuple3<Long, String, String>, GroupMetadata> groups;
        private final Map<Tuple3<Long, String, String>, FieldMetadata<?>> fields;
        private final Map<Tuple3<String, String, String>, FieldSerDes<?>> serDeses;

        private Data() {
            this.messages = new ConcurrentHashMap<>();
            this.components = new ConcurrentHashMap<>();
            this.groups = new ConcurrentHashMap<>();
            this.fields = new ConcurrentHashMap<>();
            this.serDeses = new ConcurrentHashMap<>();
        }

        private MessageMetadata getMetadata(String msgType, String fixVersion, String schemaCd) throws CacheException {
            Tuple3<String, String, String> key = Tuple.of(msgType, fixVersion, schemaCd);
            MessageMetadata metadata = messages.get(key);
            if (metadata == null) {
                try (Query<FixStructureBean> q = pm.newQuery(FixStructureBean.class, "this.msgType == :msgType")) {
                    q.setNamedParameters(Collections.singletonMap("msgType", msgType));
                    List<FixStructureBean> results = q.executeList();
                    if (results.isEmpty()) {
                        throw new NotFoundException(MessageMetadata.class, "msgType", msgType);
                    }
                    metadata = buildMessage(results.get(0), fixVersion, schemaCd);
                } catch (Exception e) {
                    throw new DataUnavailableException(e);
                }
            }
            return metadata;
        }

        private MessageMetadata buildMessage(FixStructureBean messageBean, String fixVersion, String schemaCd)
                throws CacheException {
            Tuple3<String, String, String> key = Tuple.of(messageBean.getMsgType(), fixVersion, schemaCd);
            MessageMetadata metadata = messages.get(key);
            if (metadata == null) {
                List<StructureMember> members = buildMembers(messageBean, fixVersion, schemaCd);
                metadata = new MessageMetadata(messageBean.getStructureId(), messageBean.getName(),
                        messageBean.getMsgType(), members);
                MessageMetadata _metadata = messages.putIfAbsent(key, metadata);
                if (_metadata != null) {
                    metadata = _metadata;
                }
            }
            return metadata;
        }

        private ComponentMetadata buildComponent(FixStructureBean componentBean, String fixVersion, String schemaCd)
                throws CacheException {
            Tuple3<Long, String, String> key = Tuple.of(componentBean.getStructureId(), fixVersion, schemaCd);
            ComponentMetadata metadata = components.get(key);
            if (metadata == null) {
                List<StructureMember> members = buildMembers(componentBean, fixVersion, schemaCd);
                metadata = new ComponentMetadata(componentBean.getStructureId(), componentBean.getName(), members);
                ComponentMetadata _metadata = components.putIfAbsent(key, metadata);
                if (_metadata != null) {
                    metadata = _metadata;
                }
            }
            return metadata;
        }

        private GroupMetadata buildGroup(FixStructureBean groupBean, String fixVersion, String schemaCd)
                throws CacheException {
            Tuple3<Long, String, String> key = Tuple.of(groupBean.getStructureId(), fixVersion, schemaCd);
            GroupMetadata metadata = groups.get(key);
            if (metadata == null) {
                FieldMetadata<?> numInGroupFieldMetadata = buildField(groupBean.getNoField(), fixVersion, schemaCd);
                if (numInGroupFieldMetadata.getSerDes().getValueType() != Integer.class) {
                    throw new CacheException("Group NoField must be of integer type");
                }
                List<StructureMember> members = buildMembers(groupBean, fixVersion, schemaCd);
                metadata = new GroupMetadata(groupBean.getStructureId(), groupBean.getName(),
                        (FieldMetadata<Integer>) numInGroupFieldMetadata, members);
                GroupMetadata _metadata = groups.putIfAbsent(key, metadata);
                if (_metadata != null) {
                    metadata = _metadata;
                }
            }
            return metadata;
        }

        private FieldMetadata<?> buildField(FixFieldBean fieldBean, String fixVersion, String schemaCd)
                throws CacheException {
            Tuple3<Long, String, String> key = Tuple.of(fieldBean.getFieldId(), fixVersion, schemaCd);
            FieldMetadata<?> metadata = fields.get(key);
            if (metadata == null) {
                metadata = new FieldMetadata<>(fieldBean.getFieldId(), fieldBean.getTagNum(), fieldBean.getName(),
                        buildSerDes(fieldBean.getType(), fixVersion, schemaCd));
                FieldMetadata<?> _metadata = fields.putIfAbsent(key, metadata);
                if (_metadata != null) {
                    metadata = _metadata;
                }
            }
            return metadata;
        }

        private FieldSerDes<?> buildSerDes(FixFieldTypeBean fieldTypeBean, String fixVersion, String schemaCd)
                throws CacheException {
            Tuple3<String, String, String> key = Tuple.of(fieldTypeBean.getTypeCd(), fixVersion, schemaCd);
            FieldSerDes<?> serDes = serDeses.get(key);
            if (serDes == null) {
                FixFieldTypeSpecBean specBean = fieldTypeBean.getSpec(key.second(), key.third());
                if (specBean == null) {
                    specBean = fieldTypeBean.getSpec(key.second(), null);
                }
                if (specBean == null) {
                    specBean = new FixFieldTypeSpecBean();
                }
                Field.FieldType fieldType;
                do {
                    try {
                        fieldType = Field.FieldType.valueOf(fieldTypeBean.getTypeCd());
                        serDes = FieldSerDes.getInstance(fieldType, specBean.getFormats(), specBean.getMultiplier());
                        if (serDes != null) {
                            break;
                        }
                    } catch (IllegalArgumentException e) {
                        fieldType = null;
                    }
                    fieldTypeBean = fieldTypeBean.getParent();
                } while (fieldTypeBean != null);
                if (fieldType == null) {
                    throw new NotFoundException(Field.FieldType.class, "typeCd", key.first());
                }
                FieldSerDes<?> _serDes = serDeses.putIfAbsent(key, serDes);
                if (_serDes != null) {
                    serDes = _serDes;
                }
            }
            return serDes;
        }

        private List<StructureMember> buildMembers(FixStructureBean structureBean, String fixVersion, String schemaCd)
                throws CacheException {
            List<FixStructureElementBean> elementBeans = structureBean.getElements(fixVersion, schemaCd);
            List<StructureMember> members = new ArrayList<>(elementBeans.size());
            for (FixStructureElementBean elementBean : elementBeans) {
                if (elementBean.getSubstructure() != null) {
                    FixStructureBean substructureBean = elementBean.getSubstructure();
                    switch (substructureBean.getTypeCd()) {
                        case 'C':
                            members.add(new ComponentMember(buildComponent(substructureBean, fixVersion, schemaCd),
                                    elementBean.getOrderNo(), elementBean.isTrailing(), elementBean.isRequired()));
                            break;
                        case 'G':
                            members.add(new GroupMember(buildGroup(substructureBean, fixVersion, schemaCd),
                                    elementBean.getOrderNo(), elementBean.isTrailing(), elementBean.isRequired()));
                            break;
                        default:
                            throw new CacheException("FIX structure of type \'" + substructureBean.getTypeCd() +
                                    "\' cannot be an element of another structure");
                    }
                } else {
                    members.add(new FieldMember<>(buildField(elementBean.getField(), fixVersion, schemaCd),
                            elementBean.getOrderNo(), elementBean.isTrailing(), elementBean.isRequired()));
                }
            }
            return members;
        }
    }
}
