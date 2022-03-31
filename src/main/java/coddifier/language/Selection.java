package coddifier.language;

import coddifier.db.Schema;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Selection extends UnaryExpression {

    private final Condition condition;

    public Selection(Condition condition, Expression child) {
        super(child);
        this.condition = condition;
    }

    @Override
    public boolean isWellDefined(Schema schema) {
        return getChild().isWellDefined(schema) && getChild().getSignature(schema).containsAll(condition.getSignature());
    }

    @Override
    protected boolean satisfiesSufficientConditions() {
        return true;
    }

    @Override
    protected Set<String> computeSignature(Schema schema) {
        return getChild().getSignature(schema);
    }

    @Override
    protected Set<String> computeNullableSignature(Schema schema) {
        var nullableAttributes = new HashSet<>(getChild().getNullableSignature(schema));
        nullableAttributes.removeAll(condition.getConstantSignature());
        return nullableAttributes;
    }

    @Override
    public Expression clone(List<Expression> newChildren) {
        assert newChildren.size() == 1;
        return new Selection(condition, newChildren.get(0));
    }

    @Override
    public String toString() {
        if (repr == null) {
            repr = String.format("\u03C3[%s]( %s )", condition.toString(), getChild().toString());
        }
        return repr;
    }



}
