package coddifier.transformations;

import coddifier.language.Expression;
import coddifier.language.Intersection;
import coddifier.language.Renaming;
import coddifier.language.Selection;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class IntersectionDistributor implements ExpressionTransformation {

    private final Set<Class <? extends Expression>> toPropagate = Set.of(Selection.class, Renaming.class);

    public Expression transform(Expression expression) {
        // if current node is different than <R> or <S> skip to children
        if (!toPropagate.contains(expression.getClass())) {
            List<Expression> newChildren = expression.getChildren().stream().map(this::transform).collect(Collectors.toList());
            return expression.clone(newChildren);
        }

        // if the child node is an intersection, propagate over it
        var child = expression.getChildren().get(0);
        if (child instanceof Intersection) {
            var newChildren = child.getChildren().stream().map(interChild -> expression.clone(List.of(interChild))).collect(Collectors.toList());
            return new Intersection(newChildren);
        }



        // if the child is either the selection or the renaming
        if (toPropagate.contains(child.getClass())) {
            var newChild = transform(child);
            // if the new child is an intersection we need to propagate the whole new expression again
            if (newChild instanceof Intersection) {
                return transform(expression.clone(List.of(newChild)));
            }
            // otherwise, we just return
            return expression.clone(List.of(newChild));
        }

        return expression.clone(List.of(transform(child)));
    }
}