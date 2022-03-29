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
        return isGuaranteedToPreserveCoddSemanticsAsIs(normalizedExpression, schema);
    }

    public static boolean isGuaranteedToPreserveCoddSemanticsAsIs(Expression expression, Schema schema) {
        if (!expression.isWellDefined(schema)) {
            throw new SchemaException();
        }
        return expression.isGuaranteedToPreserveCoddSemantics(schema);
    }
}
