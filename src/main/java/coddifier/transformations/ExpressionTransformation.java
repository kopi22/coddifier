package coddifier.transformations;

import coddifier.language.Expression;

public interface ExpressionTransformation {
    Expression transform(Expression subject);
}