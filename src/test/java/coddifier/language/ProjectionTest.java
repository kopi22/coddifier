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

class ProjectionTest {

    static Schema schema;
    static Relation relationR;
    static Relation relationS;
    static Relation relationT;

    static Set<String> retainedAttributes;

    @BeforeAll
    static void setUpAll() {
        var sb = new SimpleSchema.Builder();
        sb.addTable("R", Set.of(
                new Attribute("A", true),
                new Attribute("B", false),
                new Attribute("C", true)
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

        retainedAttributes = Set.of("A", "B");
    }

    @Test
    void isWellDefined_true() {
        var projection = new Projection(retainedAttributes, relationR);
        assertTrue(projection.isWellDefined(schema));
    }

    @Test
    void isWellDefined_falseChild() {
        var projection = new Projection(retainedAttributes, new Relation("Q"));
        assertFalse(projection.isWellDefined(schema));
    }

    @Test
    void isWellDefined_falseRetainedAttributes() {
        var projection = new Projection(Set.of("A", "D"), relationR);
        assertFalse(projection.isWellDefined(schema));
    }

    @Test
    void satisfiesSufficientConditions_true() {
        var projection = new Projection(retainedAttributes, relationR);
        assertTrue(projection.satisfiesSufficientConditions());
    }

    @Test
    void computeSignature() {
        var projection = new Projection(retainedAttributes, relationR);
        assertEquals(retainedAttributes, projection.computeSignature(schema));
    }

    @Test
    void computeNullableSignature() {
        var projection = new Projection(retainedAttributes, relationR);
        assertEquals(Set.of("A"), projection.computeNullableSignature(schema));
    }

    @Test
    void testClone() {
        var projectionR = new Projection(retainedAttributes, relationR);
        var projectionS = new Projection(retainedAttributes, relationS);
        assertEquals(projectionR, projectionS.clone(List.of(relationR)));
    }

    @Test
    void testClone_throwsAssertionError() {
        var projectionR = new Projection(retainedAttributes, relationR);
        assertThrows(AssertionError.class, () -> projectionR.clone(Collections.emptyList()));
        assertThrows(AssertionError.class, () -> projectionR.clone(List.of(relationR, relationS)));
    }

    @Test
    void testEquals_true() {
        var projection1 = new Projection(Set.of("A", "B"), new Relation("R"));
        var projection2 = new Projection(Set.of("B", "A"), new Relation("R"));
        assertEquals(projection1, projection2);
    }

    @Test
    void testEquals_falseDifferentChild() {
        var projectionR = new Projection(Set.of("A", "B"), new Relation("R"));
        var projectionS = new Projection(Set.of("B", "A"), new Relation("S"));
        assertNotEquals(projectionR, projectionS);
    }

    @Test
    void testEquals_falseDifferentRetainedAttributes() {
        var projectionAB = new Projection(Set.of("A", "B"), new Relation("R"));
        var projectionBC = new Projection(Set.of("B", "C"), new Relation("R"));
        assertNotEquals(projectionAB, projectionBC);
    }

    @Test
    void testToString() {
        var projection = new Projection(retainedAttributes, relationR);
        assertEquals("\u03C0[A,B]( R )", projection.toString());
    }
}