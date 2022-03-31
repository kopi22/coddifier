package coddifier.language;

import coddifier.db.Schema;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Relation extends Expression {
    private final String name;

    public Relation(String name) {
        super(Collections.emptyList());
        this.name = name;
    }

    @Override
    public boolean isWellDefined(Schema schema) {
        return schema.hasRelation(name);
    }

    @Override
    protected boolean satisfiesSufficientConditions() {
        return true;
    }

    @Override
    protected Set<String> computeSignature(Schema schema) {
        return new HashSet<>(schema.getRelationAttributeNames(name));
    }

    @Override
    protected Set<String> computeNullableSignature(Schema schema) {
        return new HashSet<>(schema.getRelationNullableAttributeNames(name));
    }

    @Override
    protected Set<String> computeBaseNames() {
        return Set.of(name);
    }

    @Override
    public Expression clone(List<Expression> newChildren) {
        assert newChildren.size() == 0;
        return new Relation(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
