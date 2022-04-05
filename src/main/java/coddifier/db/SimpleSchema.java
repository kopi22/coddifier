package coddifier.db;

import java.util.*;
import java.util.stream.Collectors;

public class SimpleSchema implements Schema {
    private final Map<String, Set<Attribute>> schema;
    private int hash;

    private SimpleSchema(Map<String, Set<Attribute>> schema) {
        this.schema = schema;
    }

    public static class Builder {
        private final Map<String, Set<Attribute>> schema = new HashMap<>();

        public Builder addTable(String tableName, Set<Attribute> attributes) {
            schema.put(tableName, attributes);
            return this;
        }

        public Builder addTable(String tableName, Attribute ...attributes) {
            schema.put(tableName, new HashSet<>(Arrays.asList(attributes)));
            return this;
        }

        public SimpleSchema build() {
            return new SimpleSchema(schema);
        }
    }

    @Override
    public Set<String> getRelationAttributeNames(String relation) {
        if (!hasRelation(relation)) {
            throw new SchemaException();  // table not present in the schema
        }
        return schema.get(relation).stream().map(Attribute::getName).collect(Collectors.toSet());
    }

    @Override
    public Set<String> getRelationNullableAttributeNames(String relation) {
        if (!hasRelation(relation)) {
            throw new SchemaException();  // table not present in the schema
        }
        return schema.get(relation).stream().filter(Attribute::getIsNullable).map(Attribute::getName).collect(Collectors.toSet());
    }

    @Override
    public boolean hasRelation(String relation) {
        return schema.containsKey(relation);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleSchema)) return false;
        SimpleSchema that = (SimpleSchema) o;
        return schema.equals(that.schema);
    }

    @Override
    public int hashCode() {
        if (hash == 0) {
            hash = Objects.hash(schema);
        }
        return hash;
    }
}
