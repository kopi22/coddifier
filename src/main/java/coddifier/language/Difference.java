package coddifier.language;

import coddifier.db.Schema;

import java.util.List;
import java.util.Set;

public class Difference extends BinaryExpression {

    public Difference(Expression leftChild, Expression rightChild) {
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
        return djn;
    }

    @Override
    protected Set<String> computeSignature(Schema schema) {
        return getLeftChild().getSignature(schema);
    }

    @Override
    protected Set<String> computeNullableSignature(Schema schema) {
        return getLeftChild().getNullableSignature(schema);
    }

    @Override
    public Expression clone(List<Expression> newChildren) {
        assert newChildren.size() == 2;
        return new Difference(newChildren.get(0), newChildren.get(1));
    }

    @Override
    public String toString() {
        if (repr == null) {
            repr = String.format("( %s ) - ( %s )", getLeftChild().toString(), getRightChild().toString());
        }
        return repr;
    }
}
