package coddifier;

import coddifier.db.Schema;
import coddifier.db.SchemaException;
import coddifier.language.Expression;
import coddifier.transformations.*;

public class Coddifier {
    private static final ExpressionTransformation irfTransformation =
            new TransformationPipeline(new IntersectionDistributor())
                .addTransformation(new IntersectionCombiner())
                .addTransformation(new IntersectionReducer());


    public static boolean isGuaranteedToPreserveCoddSemantics(Expression expression, Schema schema) {
        var normalizedExpression = irfTransformation.transform(expression);
        return normalizedExpression.isGuaranteedToPreserveCoddSemantics(schema);
    }

    public static boolean isGuaranteedToPreserveCoddSemanticsAsIs(Expression expression, Schema schema) {
        return expression.isGuaranteedToPreserveCoddSemantics(schema);
    }
}
