package me.kolek.hub.fix.serialization.determinant;

import me.kolek.determinant.DeterminantProperty;
import me.kolek.determinant.UnknownPropertyException;

import java.util.HashMap;
import java.util.Map;

public class MessagePropertyCache {
    private final Map<DeterminantProperty, MessageProperty> cache;

    public MessagePropertyCache() {
        this.cache = new HashMap<>();
    }

    public MessageProperty get(DeterminantProperty dProperty) throws UnknownPropertyException {
        MessageProperty mProperty = cache.get(dProperty);
        if (mProperty == null) {
            cache.put(dProperty, mProperty = MessageProperty.compile(dProperty));
        }
        return mProperty;
    }
}
