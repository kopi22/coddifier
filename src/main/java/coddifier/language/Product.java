package coddifier.language;

import coddifier.db.Schema;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Product extends BinaryExpression {

    public Product(Expression leftChild, Expression rightChild) {
        super(leftChild, rightChild);
    }

    @Override
    public boolean isWellDefined(Schema schema) {
        if (!(getLeftChild().isWellDefined(schema) && getRightChild().isWellDefined(schema))) {
            return false;
        }
        var commonSig = new HashSet<>(getLeftChild().getSignature(schema));
        commonSig.retainAll(getRightChild().getSignature(schema));
        return commonSig.isEmpty();
    }

    @Override
    protected boolean satisfiesSufficientConditions() {
        return nna;
    }

    @Override
    protected Set<String> computeSignature(Schema schema) {
        var attributes = new HashSet<>(getLeftChild().getSignature(schema));
        attributes.addAll(getRightChild().getSignature(schema));
        return attributes;
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
        return new Product(newChildren.get(0), newChildren.get(1));
    }

    @Override
    public String toString() {
        if (repr == null) {
            repr = String.format("( %s ) X ( %s )", getLeftChild().toString(), getRightChild().toString());
        }
        return repr;
    }
}
