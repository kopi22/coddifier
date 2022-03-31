package coddifier.language;

import coddifier.db.Schema;

import java.util.List;
import java.util.Set;

public class Distinct extends UnaryExpression {

    public Distinct(Expression child) {
        super(child);
    }

    @Override
    public boolean isWellDefined(Schema schema) {
        return getChild().isWellDefined(schema);
    }

    @Override
    protected boolean satisfiesSufficientConditions() {
        return nnc;
    }

    @Override
    protected Set<String> computeSignature(Schema schema) {
        return getChild().getSignature(schema);
    }

    @Override
    protected Set<String> computeNullableSignature(Schema schema) {
        return getChild().getNullableSignature(schema);
    }

    @Override
    public Expression clone(List<Expression> newChildren) {
        assert newChildren.size() == 1;
        return new Distinct(newChildren.get(0));
    }

    @Override
    public String toString() {
        if (repr == null) {
            repr = String.format("\u03B5( %s )", getChild().toString());
        }
        return repr;
    }
}
