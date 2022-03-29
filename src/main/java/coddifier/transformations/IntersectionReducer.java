package coddifier.transformations;

import coddifier.language.Expression;
import coddifier.language.Intersection;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class IntersectionReducer implements ExpressionTransformation {

    @Override
    public Expression transform(Expression expression) {
        if (expression instanceof Intersection) {
            var children = expression.getChildren();

            List<Expression> transformedChildren = children.stream().map(this::transform).sorted(Comparator.comparing(Object::toString)).collect(Collectors.toList());
            // there must be at least one unique child in a well-formed intersection

            List<Expression> newChildren = new ArrayList<>();
            newChildren.add(transformedChildren.get(0));

            for (int i = 1; i < transformedChildren.size(); i++) {
                if (!transformedChildren.get(i).equals(transformedChildren.get(i-1))) {
                    newChildren.add(transformedChildren.get(i));
                }
            }

            if (newChildren.size() == 1) {
                return newChildren.get(0);
            } else {
                return expression.clone(newChildren);
            }
        }

        return expression.clone(
                expression.getChildren().stream().map(this::transform).collect(Collectors.toList())
        );
    }
}
