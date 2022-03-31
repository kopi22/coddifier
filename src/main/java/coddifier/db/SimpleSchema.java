package coddifier.db;

import java.util.*;
import java.util.stream.Collectors;

public class SimpleSchema implements Schema {
    private final Map<String, Set<Attribute>> schema;

    private SimpleSchema(Map<String, Set<Attribute>> schema) {
        this.schema = schema;
    }

    public static class Builder {
        private final Map<String, Set<Attribute>> schema = new HashMap<>();

        public Set<Attribute> addTable(String tableName, Set<Attribute> attributes) {
            return schema.put(tableName, attributes);
        }

        public Set<Attribute> addTable(String tableName, Attribute ...attributes) {
            return schema.put(tableName, new HashSet<>(Arrays.asList(attributes)));
        }

        public Schema build() {
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
        return Objects.hash(schema);
    }
}
