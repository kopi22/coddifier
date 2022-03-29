package coddifier.db;

import java.util.Set;

public interface Schema {
    public Set<String> getTableAttributeNames(String tableName);
    public Set<String> getTableNullableAttributeNames(String tableName);

    public boolean hasTable(String tableName);
}
