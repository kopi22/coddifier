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

class IntersectionTest {

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
    void isWellDefined() {
        var intersection = new Intersection(relationR, relationS, relationT);
        assertTrue(intersection.isWellDefined(schema));
    }

    @Test
    void isWellDefined_falseChild () {
        var intersection = new Intersection(relationR, new Relation("Q"), relationT);
        assertFalse(intersection.isWellDefined(schema));
    }

    @Test
    void isWellDefined_falseSignature () {
        var intersection = new Intersection(new Relation("U"), relationT);
        assertFalse(intersection.isWellDefined(schema));
    }

    @Test
    void satisfiesSufficientConditions_djn() {
        var intersection = new Intersection(relationR, relationS, relationT);
        intersection.isGuaranteedToPreserveCoddSemantics(schema);
        assertTrue(intersection.djn);
        assertTrue(intersection.satisfiesSufficientConditions());
    }

    @Test
    void satisfiesSufficientConditions_false() {
        var intersection = new Intersection(relationR, relationR, relationR);
        assertFalse(intersection.isGuaranteedToPreserveCoddSemantics(schema));
        assertFalse(intersection.satisfiesSufficientConditions());
    }

    @Test
    void computeSignature() {
        var intersection = new Intersection(relationR, relationS, relationT);
        assertEquals(Set.of("A", "B"), intersection.getSignature(schema));
    }

    @Test
    void computeNullableSignature() {
        var intersection = new Intersection(relationR, relationS);
        assertEquals(Collections.emptySet(), intersection.getNullableSignature(schema));
    }

    @Test
    void testClone() {
        var intersectionRS = new Intersection(relationR, relationS);
        var intersectionRT = new Intersection(relationR, relationT);
        assertNotEquals(intersectionRS, intersectionRT);
        assertEquals(intersectionRS, intersectionRT.clone(List.of(relationR, relationS)));
    }

    @Test
    void testClone_fail() {
        var intersection = new Intersection(relationR, relationS, relationT);
        assertThrows(AssertionError.class, () -> intersection.clone(List.of(relationR)));
    }

    @Test
    void testToString_binary() {
        var intersection = new Intersection(relationR, relationS);
        assertEquals("( R ) \u2229 ( S )", intersection.toString());
    }

    @Test
    void testToString_nary() {
        var intersection = new Intersection(relationR, relationS, relationT);
        assertEquals("\u22C2( R , S , T )", intersection.toString());
    }

    @Test
    void testConstructorWithLessThanTwoChildren_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Intersection(new Relation("R")));
        assertThrows(AssertionError.class, () -> new Intersection(List.of(new Relation("R"))));
    }
}