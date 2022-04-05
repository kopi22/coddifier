package coddifier.language;

import coddifier.db.Attribute;
import coddifier.db.Schema;
import coddifier.db.SchemaException;
import coddifier.db.SimpleSchema;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ExpressionTest {

    static Schema schema;
    static Relation relationR;
    static Relation relationS;
    static Relation relationT;

    @BeforeAll
    static void setUpAll() {
        var sb = new SimpleSchema.Builder();
        sb.addTable("R", Set.of(
                new Attribute("A", true),
                new Attribute("B", false)
        ));
        sb.addTable("S", Set.of(
                new Attribute("A", false),
                new Attribute("B", true)
        ));
        sb.addTable("T", Set.of(
                new Attribute("A", false),
                new Attribute("B", false)
        ));
        sb.addTable("U", Set.of(
                new Attribute("A", false),
                new Attribute("C", false)
        ));
        schema = sb.build();

        relationR  = new Relation("R");
        relationS  = new Relation("S");
        relationT  = new Relation("T");
    }

    @Test
    void getChildren() {
        List<Expression> children = List.of(relationR, relationS, relationT);
        var intersection = new Intersection(children);
        assertEquals(children, intersection.getChildren());
    }

    @Test
    void isGuaranteedToPreserveCoddSemantics_true() {
        var customSchema = new SimpleSchema.Builder()
                .addTable("R", new Attribute("A", false), new Attribute("B", true))
                .addTable("S", new Attribute("A", true), new Attribute("B", false))
                .addTable("T", new Attribute("C", false), new Attribute("D", false))
                .build();

        var condition = new Condition() {
            @Override
            public Set<String> getSignature() {
                return Set.of("A", "B", "C");
            }

            @Override
            public String toString() {
                return "A=1 or B=C";
            }
        };

        var product = new Product(relationR, relationT);
        var selection = new Selection(condition, product);
        var projection = new Projection(Set.of("A", "C"), selection);

        var innerUnion = new Union(relationR, relationS);
        var distinct = new Distinct(relationT);
        var leftRenaming = new Renaming(Map.of("B", "C"), innerUnion);
        var rightRenaming = new Renaming(Map.of("D", "A"), distinct);
        var difference = new Difference(leftRenaming, rightRenaming);

        var outerUnion = new Union(difference, projection);

        // preserves Codd semantics
        assertTrue(outerUnion.isGuaranteedToPreserveCoddSemantics(customSchema));

        // check conditions in individual nodes
        assertTrue(outerUnion.nnc);
        assertTrue(outerUnion.djn);
        assertFalse(outerUnion.djb);
        assertFalse(outerUnion.nna);

        assertTrue(difference.nnc);
        assertTrue(difference.djn);
        assertTrue(difference.djb);
        assertFalse(difference.nna);

        assertFalse(leftRenaming.nnc);
        assertFalse(leftRenaming.djn);
        assertTrue(leftRenaming.djb);
        assertFalse(leftRenaming.nna);

        assertFalse(innerUnion.nnc);
        assertTrue(innerUnion.djn);
        assertTrue(innerUnion.djb);
        assertFalse(innerUnion.nna);

        assertTrue(rightRenaming.nnc);
        assertTrue(rightRenaming.djn);
        assertTrue(rightRenaming.djb);
        assertTrue(rightRenaming.nna);

        assertTrue(distinct.nnc);
        assertTrue(distinct.djn);
        assertTrue(distinct.djb);
        assertTrue(distinct.nna);

        assertFalse(projection.nnc);
        assertFalse(projection.djn);
        assertTrue(projection.djb);
        assertTrue(projection.nna);

        assertFalse(selection.nnc);
        assertFalse(selection.djn);
        assertTrue(selection.djb);
        assertTrue(selection.nna);

        assertTrue(product.nnc);
        assertTrue(product.djn);
        assertTrue(product.djb);
        assertTrue(product.nna);
    }

    @Test
    void isGuaranteedToPreserveCoddSemantics_throwsNotWellDefined() {
        var union = new Union(relationR, new Relation("Q"));
        assertThrows(SchemaException.class, () -> union.isGuaranteedToPreserveCoddSemantics(schema));
    }

    @Test
    void isGuaranteedToPreserveCoddSemantics_falseConditionsNotMet() {
        var union = new Union(relationR, relationR);
        assertFalse(union.isGuaranteedToPreserveCoddSemantics(schema));
    }

    @Test
    void getSignature_testCaching() {
        var intersection = new Intersection(relationR, relationS, relationT);
        var sig1 = intersection.getSignature(schema);
        var sig2 = intersection.getSignature(schema);
        assertSame(sig1, sig2);
    }

    @Test
    void getNullableSignature_testCaching() {
        var intersection = new Intersection(relationR, relationS, relationT);
        var sig1 = intersection.getNullableSignature(schema);
        var sig2 = intersection.getNullableSignature(schema);
        assertSame(sig1, sig2);
    }

    @Test
    void getBaseNames_testCaching() {
        var intersection = new Intersection(relationR, relationS, relationT);
        var base1 = intersection.getBaseNames();
        var base2 = intersection.getBaseNames();
        assertSame(base1, base2);
    }

    @Test
    void computeBaseNames() {
        var relationU = new Relation("U");
        var expression = new Product(
            new Renaming(Map.of("A", "D"), relationU),
            new Intersection(relationR, relationS, relationT)
        );
        assertEquals(
            Set.of("R", "S", "T", "U"),
            expression.computeBaseNames()
        );
    }

    @Test
    void testHashCode_sameExprHaveTheSameHash() {
        var intersection1 = new Intersection(relationR, relationS);
        var intersection2 = new Intersection(new Relation("R"), new Relation("S"));
        assertEquals(intersection1.hashCode(), intersection1.hashCode());
        assertEquals(intersection1.hashCode(), intersection2.hashCode());
    }

    // for the sake of completness - to cover a branch in equals method
    @Test
    void testEquals_notAExpression() {
        assertNotEquals(relationR, "R");
    }
}