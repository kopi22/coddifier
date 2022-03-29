package coddifier.transformations;

import coddifier.language.Expression;

public class TransformationPipeline implements ExpressionTransformation {

    private final ExpressionTransformation transformation;

    public TransformationPipeline(ExpressionTransformation transformation) {
        this.transformation = transformation;
    }

    public TransformationPipeline addTransformation(ExpressionTransformation newTransformation) {
        return new TransformationPipeline(input -> newTransformation.transform(transformation.transform(input)));
    }

    @Override
    public Expression transform(Expression subject) {
        return transformation.transform(subject);
    }
}
