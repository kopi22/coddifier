package coddifier.language;

import java.util.List;

abstract class BinaryExpression extends Expression {

    public BinaryExpression(Expression leftChild, Expression rightChild) {
        super(List.of(leftChild, rightChild));
    }

    public Expression getLeftChild() {
        return children.get(0);
    }

    public Expression getRightChild() {
        return children.get(1);
    }
}
