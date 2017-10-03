package me.kolek.hub.determinant;

import me.kolek.determinant.DeterminantCriteria;
import me.kolek.determinant.DeterminantEvaluable;
import me.kolek.determinant.DeterminantException;

public class Determinant extends me.kolek.determinant.Determinant {
    private final long id;
    private final String name;
    private final String description;
    private final String type;
    private final String typeDescription;
    private final double priority;
    private final boolean active;

    public Determinant(long id, String name, String description, String type, String typeDescription, double priority, boolean active,
            DeterminantCriteria... criteria) {
        super(criteria);
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.typeDescription = typeDescription;
        this.priority = priority;
        this.active = active;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public String getTypeDescription() {
        return typeDescription;
    }

    public double getPriority() {
        return priority;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public boolean matches(DeterminantEvaluable evaluable) throws DeterminantException {
        return active && super.matches(evaluable);
    }

    @Override
    public String toString() {
        return "[" + id + "] " + name + ": " + description;
    }
}
