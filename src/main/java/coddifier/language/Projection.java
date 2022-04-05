package coddifier.language;

import coddifier.db.Schema;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Projection extends UnaryExpression {
    private final Set<String> retainedAttributes;

    public Projection(Set<String> retainedAttributes, Expression child) {
        super(child);
        this.retainedAttributes = retainedAttributes;
    }

    @Override
    public boolean isWellDefined(Schema schema) {
        return getChild().isWellDefined(schema) && getChild().getSignature(schema).containsAll(retainedAttributes);
    }

    @Override
    protected boolean satisfiesSufficientConditions() {
        return true;
    }

    @Override
    protected Set<String> computeSignature(Schema schema) {
        return retainedAttributes;
    }

    @Override
    protected Set<String> computeNullableSignature(Schema schema) {
        var nullableAttributes = new HashSet<>(getChild().getNullableSignature(schema));
        nullableAttributes.retainAll(retainedAttributes);
        return nullableAttributes;
    }

    @Override
    public Expression clone(List<Expression> newChildren) {
        assert newChildren.size() == 1;
        return new Projection(retainedAttributes, newChildren.get(0));
    }

    @Override
    public String toString() {
        if (repr == null) {
            repr = String.format("\u03C0[%s]( %s )", retainedAttributes.stream().sorted().collect(Collectors.joining(",")), getChild().toString());
        }
        return repr;
    }


}
