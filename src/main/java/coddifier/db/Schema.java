package coddifier.db;

import java.util.Set;

public interface Schema {
    boolean hasRelation(String relation);
    Set<String> getRelationAttributeNames(String relation);
    Set<String> getRelationNullableAttributeNames(String relation);
}
