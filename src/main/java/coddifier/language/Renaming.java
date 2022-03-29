package coddifier.language;

import coddifier.db.Schema;
import coddifier.db.SchemaException;

import java.util.*;

public class Renaming extends UnaryExpression {

    private final Map<String, String> renamings;

    public Renaming(Map<String, String> renamings, Expression child) {
        super(child);
        this.renamings = renamings;
    }

    @Override
    public boolean isWellDefined(Schema schema) {
        if (!getChild().isWellDefined(schema)) {
            return false;
        }
        var childSig = getChild().getSignature(schema);
        for (var renaming: renamings.entrySet()) {
            if (!(childSig.contains(renaming.getKey()) && (!childSig.contains(renaming.getValue()) || renaming.getKey().equals(renaming.getValue())))) {
                return false;
            }
        }
        return isInjectiveMap(renamings);
    }

    @Override
    protected boolean isMarked() {
        return true;
    }

    @Override
    protected Set<String> computeSignature(Schema schema) {
        var newAttributes = new HashSet<>(getChild().getSignature(schema));
        for (var renaming : renamings.entrySet()) {
            if (!newAttributes.contains(renaming.getKey())) {
                throw new SchemaException();
            }
            newAttributes.remove(renaming.getKey());
            newAttributes.add(renaming.getValue());
        }
        return newAttributes;
    }

    @Override
    protected Set<String> computeNullableSignature(Schema schema) {
        var newNullableAttributes = new HashSet<>(getChild().getNullableSignature(schema));
        for (var renaming : renamings.entrySet()) {
            if (newNullableAttributes.contains(renaming.getKey())) {
                newNullableAttributes.remove(renaming.getKey());
                newNullableAttributes.add(renaming.getValue());
            }
        }
        return newNullableAttributes;
    }

    private static<K,V> boolean isInjectiveMap(Map<K,V> input) {
        Map<V,K> inv = new HashMap<V,K>();
        for (Map.Entry<K,V> entry : input.entrySet()) {
            if (inv.containsKey(entry.getValue())) {
                return false;
            } else {
                inv.put(entry.getValue(), entry.getKey());
            }
        }
        return true;
    }

    @Override
    public Expression clone(List<Expression> newChildren) {
        assert newChildren.size() == 1;
        return new Renaming(renamings, newChildren.get(0));
    }

    @Override
    public String toString() {
        if (repr == null) {
            String repl = renamings.toString()
                    .replace(" ", "")
                    .replace("{", "[")
                    .replace("}", "]")
                    .replace("=", "->");
            repr = String.format("\u03c1%s( %s )", repl, getChild().toString());
        }
        return repr;
    }
}
