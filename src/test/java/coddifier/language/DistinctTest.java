package coddifier.language;

import coddifier.db.Attribute;
import coddifier.db.Schema;
import coddifier.db.SimpleSchema;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DistinctTest {

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
        assertTrue(new Distinct(relationR).isWellDefined(schema));
    }

    @Test
    void isWellDefined_false() {
        assertFalse(new Distinct(new Relation("Q")).isWellDefined(schema));
    }

    @Test
    void satisfiesSufficientConditions_nnc() {
        var distinct = new Distinct(relationT);
        distinct.isGuaranteedToPreserveCoddSemantics(schema);
        assertTrue(distinct.nnc);
        assertTrue(distinct.satisfiesSufficientConditions());
    }

    @Test
    void satisfiesSufficientConditions_false() {
        var distinct = new Distinct(relationR);
        assertFalse(distinct.isGuaranteedToPreserveCoddSemantics(schema));
        assertFalse(distinct.satisfiesSufficientConditions());
    }

    @Test
    void computeSignature() {
        assertEquals(Set.of("A", "B"), new Distinct(relationR).getSignature(schema));
    }

    @Test
    void computeNullableSignature() {
        assertEquals(Set.of("B"), new Distinct(relationS).getNullableSignature(schema));
    }

    @Test
    void testClone() {
        var distinctS = new Distinct(relationS);
        var distinctT = new Distinct(relationT);
        assertNotEquals(distinctS, distinctT);
        assertEquals(distinctS, distinctT.clone(List.of(relationS)));
    }

    @Test
    void testClone_throwsAssertion() {
        var distinct = new Distinct(relationR);
        assertThrows(AssertionError.class, () -> distinct.clone(List.of(relationR, relationT)));
        assertThrows(AssertionError.class, () -> distinct.clone(Collections.emptyList()));
    }

    @Test
    void testToString() {
        var distinct = new Distinct(relationR);
        assertEquals("\u03B5( R )", distinct.toString());
    }
}