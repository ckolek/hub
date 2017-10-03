package me.kolek.hub.determinant;

public class DeterminantProperty extends me.kolek.determinant.DeterminantProperty {
    private final long id;

    public DeterminantProperty(long id, String name) {
        super(name);
        this.id = id;
    }

    public long getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return (int) id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (obj.getClass() != DeterminantProperty.class) {
            return false;
        }

        DeterminantProperty other = (DeterminantProperty) obj;
        return (this.id == other.id);
    }
}
