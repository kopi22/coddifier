package coddifier.transformations;

import coddifier.language.Expression;
import coddifier.language.Intersection;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IntersectionCombiner implements ExpressionTransformation {
    @Override
    public Expression transform(Expression expression) {
        if (expression instanceof Intersection) {
            List<Expression> newChildren = expression.getChildren().stream().flatMap(e -> {
                if (e instanceof Intersection) {
                    return transform(e).getChildren().stream();
                } else {
                    return Stream.of(transform(e));
                }
            }).collect(Collectors.toList());

            return new Intersection(newChildren);
        }
        List<Expression> newChildren = expression.getChildren().stream().map(this::transform).collect(Collectors.toList());
        return expression.clone(newChildren);
    }
}
