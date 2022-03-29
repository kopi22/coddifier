package coddifier.language;

import java.util.List;

abstract class UnaryExpression extends Expression {

    protected UnaryExpression(Expression child) {
        super(List.of(child));
    }

    public Expression getChild() {
        return children.get(0);
    }
}
