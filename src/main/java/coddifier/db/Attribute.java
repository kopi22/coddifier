package coddifier.db;

import java.util.Objects;

public class Attribute {
    private final String name;
    private final boolean isNullable;

    public Attribute(String name) {
        this.name = name;
        this.isNullable = true;
    }

    public Attribute(String name, boolean isNullable) {
        this.name = name;
        this.isNullable = isNullable;
    }

    public String getName() {
        return name;
    }

    public boolean getIsNullable() {
        return isNullable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Attribute)) return false;
        Attribute attribute = (Attribute) o;
        return name.equals(attribute.name) && isNullable == attribute.isNullable;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, isNullable);
    }
}
