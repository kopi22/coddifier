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

class RelationTest {

    static Schema schema;
    static Relation relationR;
    static Relation relationS;

    @BeforeAll
    static void setUpAll() {
        var sb = new SimpleSchema.Builder();
        sb.addTable("R", Set.of(
                new Attribute("A", true),
                new Attribute("B", false)
        ));
        sb.addTable("S", Set.of(
                new Attribute("A", false)
        ));
        schema = sb.build();

        relationR  = new Relation("R");
        relationS  = new Relation("S");
    }

    @Test
    void test_getChildren() {
        assert relationR.getChildren().isEmpty();
    }

    @Test
    void test_isGuaranteedToPreserveCoddSemantics() {
        assertTrue(relationR.isGuaranteedToPreserveCoddSemantics(schema));
    }

    @Test
    void test_satisfiesNNC() {
        assertTrue(relationR.satisfiesNNC(schema));
    }

    @Test
    void test_satisfiesNNA_nullable() {
        assertFalse(relationR.satisfiesNNA(schema, false));
    }

    @Test
    void test_satisfiesNNA_nonnullable() {
        assertTrue(relationS.satisfiesNNA(schema, false));
    }

    @Test
    void test_satisfiesDJN() {
        assertTrue(relationR.satisfiesDJN(schema));

    }

    @Test
    void test_satisfiesDJB() {
        assertTrue(relationR.satisfiesDJB());
    }

    @Test
    void test_equals_same() {
        assertEquals(relationR, new Relation("R"));
    }

    @Test
    void test_equals_different() {
        assertNotEquals(relationR, relationS);
    }

    @Test
    void test_isWellDefined_true() {
        assertTrue(relationR.isWellDefined(schema));
    }

    @Test
    void test_isWellDefined_false() {
        assertFalse(new Relation("T").isWellDefined(schema));
    }

    @Test
    void test_isMarked() {
        assertTrue(relationR.satisfiesSufficientConditions());
    }

    @Test
    void test_computeSignature() {
        assertEquals(Set.of("A", "B"), relationR.computeSignature(schema));
    }

    @Test
    void test_computeNullableSignature() {
        assertEquals(Set.of("A"), relationR.computeNullableSignature(schema));
    }

    @Test
    void test_computeBaseNames() {
        assertEquals(Set.of("R"), relationR.computeBaseNames());
    }

    @Test
    void test_clone() {
        assertEquals(relationR, relationR.clone(Collections.emptyList()));
    }

    @Test
    void test_clone_withChildren() {
        assertThrows(AssertionError.class, () -> relationR.clone(List.of(relationS)));
    }

    @Test
    void test_toString() {
        assertEquals("R", relationR.toString());
    }
}