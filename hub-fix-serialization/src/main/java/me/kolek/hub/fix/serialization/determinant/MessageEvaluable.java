package me.kolek.hub.fix.serialization.determinant;

import me.kolek.determinant.DeterminantEvaluable;
import me.kolek.determinant.DeterminantProperty;
import me.kolek.determinant.UnknownPropertyException;
import me.kolek.fix.serialization.Message;

public class MessageEvaluable implements DeterminantEvaluable {
    private final Message message;
    private final MessagePropertyCache cache;

    public MessageEvaluable(Message message, MessagePropertyCache cache) {
        this.message = message;
        this.cache = cache;
    }

    @Override
    public Object getValue(DeterminantProperty determinantProperty) throws UnknownPropertyException {
        return cache.get(determinantProperty).getValue(message);
    }
}
