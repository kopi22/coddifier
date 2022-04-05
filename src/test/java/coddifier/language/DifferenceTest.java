package coddifier.language;

import coddifier.db.Attribute;
import coddifier.db.Schema;
import coddifier.db.SimpleSchema;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DifferenceTest {

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
    void isWellDefined_true() {
        assertTrue(new Difference(relationR, relationS).isWellDefined(schema));
    }

    @Test
    void isWellDefined_falseLeftChild() {
        assertFalse(new Difference(new Relation("Q"), relationS).isWellDefined(schema));
    }

    @Test
    void isWellDefined_falseRightChild() {
        assertFalse(new Difference(relationS, new Relation("Q")).isWellDefined(schema));
    }

    @Test
    void isWellDefined_falseSchemaMismatch() {
        assertFalse(new Difference(relationR, new Relation("U")).isWellDefined(schema));
    }

    @Test
    void satisfiesSufficientConditions_djn() {
        var difference = new Difference(relationR, relationS);
        difference.isGuaranteedToPreserveCoddSemantics(schema);
        assertTrue(difference.djn);
        assertTrue(difference.satisfiesSufficientConditions());
    }

    @Test
    void satisfiesSufficientConditions_false() {
        var difference = new Difference(relationR, relationR);
        assertFalse(difference.isGuaranteedToPreserveCoddSemantics(schema));
        assertFalse(difference.satisfiesSufficientConditions());
    }

    @Test
    void computeSignature() {
        var difference = new Difference(relationR, relationS);
        assertEquals(Set.of("A", "B"), difference.getSignature(schema));
    }

    @Test
    void computeNullableSignature() {
        var difference = new Difference(relationR, relationS);
        assertEquals(Set.of("A"), difference.getNullableSignature(schema));
    }

    @Test
    void testClone() {
        var differenceRS = new Difference(relationR, relationS);
        var differenceRT = new Difference(relationR, relationT);
        assertNotEquals(differenceRS, differenceRT);
        assertEquals(differenceRS, differenceRT.clone(List.of(relationR, relationS)));
    }

    @Test
    void testClone_throwsAssertion() {
        var difference = new Difference(relationR, relationS);
        assertThrows(AssertionError.class, () -> difference.clone(List.of(relationT)));
    }

    @Test
    void testToString() {
        var difference = new Difference(relationR, relationS);
        assertEquals("( R ) - ( S )", difference.toString());
    }
}