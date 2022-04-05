package coddifier.language;

import coddifier.db.Schema;
import coddifier.db.SchemaException;

import java.util.*;

public abstract class Expression {
    protected final List<Expression> children;

    protected String repr;
    private int hash;

    // as expressions are immutable, their properties can be cached
    private final Map<Schema, Set<String>> signatureCache = new HashMap<>();
    private final Map<Schema, Set<String>> nullableSignatureCache = new HashMap<>();
    private Set<String> baseCache;

    protected boolean djb = false;
    protected boolean djn = false;
    protected boolean nnc = false;
    protected boolean nna = false;

    protected Expression(List<Expression> children) {
        this.children = Collections.unmodifiableList(children);
    }

    public List<Expression> getChildren() {
        return children;
    }

    public abstract boolean isWellDefined(Schema schema);

    public boolean isGuaranteedToPreserveCoddSemantics(Schema schema) {
        // step 1: ensure the expression is well-defined
        if (!isWellDefined(schema)) {
            throw new SchemaException();
        }

        // step 2: compute properties
        getNullableSignature(schema);
        getBaseNames();

        // step 3: annotate syntax tree
        annotateWithSufficientConditions(schema, false);

        // step 4: check if every node meets its sufficient condition
        return isSyntaxTreeMarked();
    }

    private void annotateWithSufficientConditions(Schema schema, boolean hasNonnullableAncestor) {
        satisfiesNNC(schema);
        satisfiesDJN(schema);
        satisfiesDJB();
        satisfiesNNA(schema, hasNonnullableAncestor);

        children.forEach(e -> e.annotateWithSufficientConditions(schema, nna));
    }

    private boolean isSyntaxTreeMarked() {
        if (!satisfiesSufficientConditions()) {
            // if the node is not marked, then query does not meet sufficient conditions
            return false;
        }

        for (var e : children) {
            // if the subtree is not fully marked, then query does not meet sufficient conditions
            if (!e.isSyntaxTreeMarked()) {
                return false;
            }
        }

        return true;
    }

    protected abstract boolean satisfiesSufficientConditions();

    protected boolean satisfiesNNC(Schema schema) {
        if (children.isEmpty()) {
            nnc = true;
            return true;
        }

        for (Expression e : children) {
            if (e.getNullableSignature(schema).isEmpty()) {
                nnc = true;
                return true;
            }
        }
        nnc = false;
        return false;
    }

    protected boolean satisfiesNNA(Schema schema, boolean hasNonnullableAncestor) {
        if (hasNonnullableAncestor) {
            nna = true;
            return true;
        }
        if (getNullableSignature(schema).isEmpty()) {
            nna = true;
            return true;
        }
        nna = false;
        return false;
    }

    protected boolean satisfiesDJN(Schema schema) {
        if (children.isEmpty()) {
            djn = true;
            return true;
        }

        var nsig = new HashSet<>(children.get(0).getNullableSignature(schema));
        for (Expression e : children.subList(1, children.size())) {
            nsig.retainAll(e.getNullableSignature(schema));
        }

        if (nsig.isEmpty()) {
            djn = true;
            return true;
        }

        djn = false;
        return false;
    }

    protected boolean satisfiesDJB() {
        if (children.isEmpty()) {
            djb = true;
            return true;
        }

        Set<String> basesSoFar = new HashSet<>();
        for (Expression e : children) {
            var baseNames = e.getBaseNames();
            for (String relationName : baseNames) {
                if (basesSoFar.contains(relationName)) {
                    djb = false;
                    return false;
                }
                basesSoFar.add(relationName);
            }
        }
        djb = true;
        return true;
    }

    public Set<String> getSignature(Schema schema) {
        return signatureCache.computeIfAbsent(schema, s -> Collections.unmodifiableSet(computeSignature(s)));
    }

    protected abstract Set<String> computeSignature(Schema schema);

    public Set<String> getNullableSignature(Schema schema) {
        return nullableSignatureCache.computeIfAbsent(schema, s -> Collections.unmodifiableSet(computeNullableSignature(s)));
    }

    protected abstract Set<String> computeNullableSignature(Schema schema);

    public Set<String> getBaseNames() {
        if (baseCache == null) {
            baseCache = Collections.unmodifiableSet(computeBaseNames());
        }
        return baseCache;
    }

    protected Set<String> computeBaseNames() {
        var baseNames = new HashSet<String>();
        for (var e : children) {
            baseNames.addAll(e.getBaseNames());
        }
        return baseNames;
    }

    public abstract Expression clone(List<Expression> newChildren);

    @Override
    public int hashCode() {
        if (hash == 0) {
            hash = Objects.hash(toString());
        }
        return hash;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof Expression) {
            var otherExpression = (Expression) other;
            return this.toString().equals(otherExpression.toString());
        }
        return false;
    }

    @Override
    public abstract String toString();
}
