package me.kolek.hub.determinant.cache;

import me.kolek.determinant.*;
import me.kolek.determinant.value.*;
import me.kolek.hub.cache.AbstractCache;
import me.kolek.hub.cache.CacheException;
import me.kolek.hub.cache.DataUnavailableException;
import me.kolek.hub.cache.NotFoundException;
import me.kolek.hub.determinant.Determinant;
import me.kolek.hub.determinant.DeterminantProperty;
import me.kolek.hub.determinant.data.*;
import me.kolek.util.CollectionUtil;
import me.kolek.util.tuple.Tuple;
import me.kolek.util.tuple.Tuple2;

import javax.inject.Inject;
import javax.inject.Named;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.management.MalformedObjectNameException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class DeterminantCacheImpl extends AbstractCache implements DeterminantCache {
    private final PersistenceManager pm;
    private final AtomicReference<Data> reference;

    @Inject
    public DeterminantCacheImpl(@Named("Determinant") PersistenceManager pm) throws MalformedObjectNameException {
        super("Determinant");
        this.pm = pm;
        this.reference = new AtomicReference<>();
    }

    @Override
    public List<Determinant> getByType(String determinantType) throws CacheException {
        if (needsLoad()) {
            load();
        }

        Map<String, List<Determinant>> determinantsByType =
                getData().map(d -> d.determinantsByType).orElseGet(Collections::emptyMap);
        List<Determinant> determinants = determinantsByType.get(determinantType);
        if (determinants == null) {
            try (Query<DeterminantBean> q = pm
                    .newQuery(DeterminantBean.class, "type.typeCd == typeCd && parent == null")) {
                q.declareParameters("String typeCd");
                q.setParameters(determinantType);
                List<DeterminantBean> results = q.executeList();
                if (!results.isEmpty()) {
                    determinants = new ArrayList<>();
                    DeterminantBuilder builder = new DeterminantBuilder();
                    for (DeterminantBean _determinant : results) {
                        determinants.addAll(builder.build(_determinant));
                    }
                    determinants.sort(Comparator.comparingDouble(Determinant::getPriority));
                    determinants = Collections.unmodifiableList(determinants);
                } else {
                    determinants = Collections.emptyList();
                }

                determinantsByType = new HashMap<>(determinantsByType);
                determinantsByType.put(determinantType, determinants);

                synchronized (this) {
                    reference.set(new Data(Collections.unmodifiableMap(determinantsByType)));
                }
            } catch (CacheException e) {
                throw e;
            } catch (Exception e) {
                throw new DataUnavailableException(e);
            }
        }

        if (determinants.isEmpty()) {
            throw new NotFoundException(Determinant.class, "type", determinantType);
        }

        return determinants;
    }

    @Override
    public Optional<Determinant> getMatch(String determinantType, DeterminantEvaluable evaluable)
            throws CacheException, DeterminantException {
        return me.kolek.determinant.Determinant.getMatch(getByType(determinantType), evaluable);
    }

    @Override
    public List<Determinant> getMatches(String determinantType, DeterminantEvaluable evaluable)
            throws CacheException, DeterminantException {
        return me.kolek.determinant.Determinant.getMatches(getByType(determinantType), evaluable);
    }

    public Optional<Data> getData() {
        return Optional.ofNullable(reference.get());
    }

    @Override
    protected void _load() throws DataUnavailableException {
        reference.set(new Data(Collections.emptyMap()));
    }

    private static class Data {
        private final Map<String, List<Determinant>> determinantsByType;

        private Data(Map<String, List<Determinant>> determinantsByType) {
            this.determinantsByType = Collections.unmodifiableMap(determinantsByType);
        }
    }

    private static class DeterminantBuilder {
        private final Map<Long, DeterminantProperty> properties = new HashMap<>();

        private List<Determinant> build(DeterminantBean _determinant) throws CacheException {
            List<Determinant> determinants = new ArrayList<>();
            buildDeterminant(null, _determinant, determinants);
            return determinants;
        }

        private void buildDeterminant(Determinant parent, DeterminantBean _determinant, List<Determinant> determinants)
                throws CacheException {
            List<DeterminantCriteria> criteria = new ArrayList<>();
            Collections.addAll(criteria, buildCriteriaChildren(_determinant.getUnions(), _determinant.getCriteria()));
            if (parent != null) {
                criteria.addAll(0, Arrays.asList(parent.getCriteria()));
            }

            Determinant determinant =
                    new Determinant(_determinant.getDetermId(), _determinant.getName(), _determinant.getDescription(),
                            _determinant.getType().getTypeCd(), _determinant.getType().getDescription(),
                            _determinant.getPriority(), _determinant.getActive(),
                            CollectionUtil.toArray(criteria, DeterminantCriteria[]::new));
            determinants.add(determinant);

            for (DeterminantBean childDeterminant : _determinant.getChildren()) {
                buildDeterminant(determinant, childDeterminant, determinants);
            }
        }

        private DeterminantCriteria[] buildCriteriaChildren(List<DeterminantCriterionUnionBean> unions,
                List<DeterminantCriterionBean> criteria) throws CacheException {
            List<Tuple2<Integer, DeterminantCriteria>> childCriteria = new ArrayList<>();

            for (DeterminantCriterionUnionBean union : unions) {
                childCriteria.add(Tuple.of(union.getOrderNo(), buildCriteria(union)));
            }

            for (DeterminantCriterionBean criterion : criteria) {
                childCriteria.add(Tuple.of(criterion.getOrderNo(), buildCriteria(criterion)));
            }

            return childCriteria.stream().sorted(Comparator.comparingInt(Tuple2::first)).map(Tuple2::second)
                    .toArray(DeterminantCriteria[]::new);
        }

        private DeterminantCriteria buildCriteria(DeterminantCriterionUnionBean union) throws CacheException {
            return new ComplexDeterminantCriteria(buildCriteriaChildren(union.getChildren(), union.getCriteria()),
                    union.isNegated(), union.isShortCircuited());
        }

        private DeterminantCriteria buildCriteria(DeterminantCriterionBean criterion) throws CacheException {
            DeterminantPropertyBean _property = criterion.getProperty();
            DeterminantProperty property = properties.get(_property.getPropId());
            if (property == null) {
                properties.put(_property.getPropId(),
                        property = new DeterminantProperty(_property.getPropId(), _property.getName()));
            }

            DeterminantOperator operator = getOperator(criterion.getOperatorCd());

            Collection<DeterminantCriterionValueBean> _values = criterion.getValues();

            DeterminantValue<?> value;
            if (_values.isEmpty()) {
                value = createValue(criterion.getValueTypeCd(), null);
            } else if (_values.size() == 1) {
                DeterminantCriterionValueBean _value = CollectionUtil.first(_values);
                value = createValue(criterion.getValueTypeCd(), _value.getValue(), _value.getSuppValue1(),
                        _value.getSuppValue2());
            } else {
                List<SingleValue> values = new ArrayList<>();
                for (DeterminantCriterionValueBean _value : _values) {
                    values.add(createValue(criterion.getValueTypeCd(), _value.getValue(), _value.getSuppValue1(),
                            _value.getSuppValue2()));
                }
                value = new MultiValue<>(values);
            }

            return new SimpleDeterminantCriteria<>(property, value, criterion.isNegated(), operator,
                    criterion.isShortCircuited());
        }

        private static DeterminantOperator getOperator(String operatorCd) throws CacheException {
            switch (operatorCd) {
                case "=":
                    return DeterminantOperators.EQUAL_TO;
                case "<":
                    return DeterminantOperators.LESS_THAN;
                case "<=":
                    return DeterminantOperators.LESS_THAN_OR_EQUAL_TO;
                case ">":
                    return DeterminantOperators.GREATER_THAN;
                case ">=":
                    return DeterminantOperators.GREATER_THAN_OR_EQUAL_TO;
                case "=~":
                    return DeterminantOperators.SIMILAR_TO;
                case "NULL":
                    return DeterminantOperators.IS_NULL;
                default:
                    throw new CacheException("invalid determinant criterion operator: " + operatorCd);
            }
        }

        private static SingleValue<?> createValue(String valueTypeCd, String _value, String... suppValues)
                throws CacheException {
            SingleValue<?> value;
            switch (valueTypeCd) {
                case "BOOL":
                    value = BooleanValue.valueOf(_value);
                    break;
                case "DATE":
                    value = DateValue.valueOf(_value);
                    break;
                case "DEC":
                    value = DecimalValue.valueOf(_value);
                    break;
                case "DOM":
                    value = DayOfMonthValue.valueOf(_value);
                    break;
                case "DOW":
                    value = DayOfWeekValue.valueOf(_value);
                    break;
                case "INT":
                    value = IntegerValue.valueOf(_value);
                    break;
                case "MONTH":
                    value = MonthValue.valueOf(_value);
                    break;
                case "PTRN":
                    value = PatternValue.valueOf(_value);
                    break;
                case "STR":
                    value = StringValue.valueOf(_value);
                    break;
                case "TIME":
                    value = TimeValue.valueOf(_value);
                    break;
                case "YEAR":
                    value = YearValue.valueOf(_value);
                    break;
                default:
                    throw new CacheException("invalid determinant criterion value type: " + valueTypeCd);
            }

            if (value.getValue() != null ^ _value != null) {
                throw new CacheException(_value + " cannot be coerced to " + valueTypeCd);
            }

            return value;
        }
    }
}
