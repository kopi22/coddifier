package coddifier.language;

import coddifier.db.Schema;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Intersection extends Expression {

    public Intersection(List<Expression> children) {
        super(children);
        assert children.size() >= 2;
    }

    public Intersection(Expression ...children) {
        super(Arrays.asList(children));
        assert children.length >= 2;
    }

    @Override
    public boolean isWellDefined(Schema schema) {
        var sig = getSignature(schema);
        return getChildren().stream().allMatch(e -> e.isWellDefined(schema))
                && getChildren().stream().map(e -> e.getSignature(schema)).allMatch(sig::equals);
    }

    @Override
    protected boolean isMarked() {
        return djn;
    }

    @Override
    protected Set<String> computeSignature(Schema schema) {
        return children.get(0).getSignature(schema);
    }

    @Override
    protected Set<String> computeNullableSignature(Schema schema) {
        var nullableAttributes = new HashSet<>(children.get(0).getNullableSignature(schema));
        for (var i = 1; i < children.size(); i++) {
            nullableAttributes.retainAll(children.get(i).getNullableSignature(schema));
        }
        return nullableAttributes;
    }

    @Override
    public Expression clone(List<Expression> newChildren) {
        assert newChildren.size() >= 2;
        return new Intersection(newChildren);
    }

    @Override
    public String toString() {
        if (repr == null) {
            if (children.size() == 2) {
                repr = String.format("( %s ) \u2229 ( %s )", children.get(0).toString(), children.get(1).toString());
            } else {
                repr = String.format("\u22C2( %s )", children.stream().map(Expression::toString).collect(Collectors.joining(" , ")));
            }
        }
        return repr;
    }
}
