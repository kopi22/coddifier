package coddifier.language;

import coddifier.db.Schema;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Union extends BinaryExpression {

    public Union(Expression leftChild, Expression rightChild) {
        super(leftChild, rightChild);
    }

    @Override
    public boolean isWellDefined(Schema schema) {
        return getLeftChild().isWellDefined(schema)
                && getRightChild().isWellDefined(schema)
                && getLeftChild().getSignature(schema).equals(getRightChild().getSignature(schema));
    }

    @Override
    protected boolean satisfiesSufficientConditions() {
        return djb || nnc || nna;
    }

    @Override
    protected Set<String> computeSignature(Schema schema) {
        return children.get(0).getSignature(schema);
    }

    @Override
    protected Set<String> computeNullableSignature(Schema schema) {
        var nullableAttributes = new HashSet<>(getLeftChild().getNullableSignature(schema));
        nullableAttributes.addAll(getRightChild().getNullableSignature(schema));
        return nullableAttributes;
    }

    @Override
    public Expression clone(List<Expression> newChildren) {
        assert newChildren.size() == 2;
        return new Union(newChildren.get(0), newChildren.get(1));
    }

    @Override
    public String toString() {
        if (repr == null) {
            repr = String.format("( %s ) \u222A ( %s )", getLeftChild().toString(), getRightChild().toString());
        }
        return repr;
    }
}
