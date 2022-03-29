package coddifier.db;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SimpleSchema implements Schema {
    private final Map<String, Set<Attribute>> schema = new HashMap<>();

    public Set<Attribute> addTable(String tableName, Set<Attribute> attributes) {
        return schema.put(tableName, attributes);
    }

    @Override
    public Set<String> getTableAttributeNames(String tableName) {
        if (!hasTable(tableName)) {
            throw new SchemaException();  // table not present in the schema
        }
        return schema.get(tableName).stream().map(Attribute::getName).collect(Collectors.toSet());
    }

    @Override
    public Set<String> getTableNullableAttributeNames(String tableName) {
        if (!hasTable(tableName)) {
            throw new SchemaException();  // table not present in the schema
        }
        return schema.get(tableName).stream().filter(Attribute::getIsNullable).map(Attribute::getName).collect(Collectors.toSet());
    }

    @Override
    public boolean hasTable(String tableName) {
        return schema.containsKey(tableName);
    }
}
