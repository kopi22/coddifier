package coddifier.language;

import coddifier.db.Attribute;
import coddifier.db.SimpleSchema;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RenamingTest {

    static SimpleSchema schema;
    static Relation relationR;

    static Map<String, String> replacements;
    Renaming renaming;

    @BeforeAll
    static void setUpAll() {
        schema = new SimpleSchema();
        schema.addTable("R", Set.of(
                new Attribute("A", false),
                new Attribute("B", true)
        ));

        relationR  = new Relation("R");

        replacements = Map.of(
                "A", "C",
                "B", "D"
        );
    }

    @BeforeEach
    void setUp() {
        renaming = new Renaming(replacements, relationR);
    }

    @Test
    void isWellDefined() {
        assertTrue(renaming.isWellDefined(schema));
    }

    @Test
    void isWellDefined_mappingToTheSameAttribute() {
        assertTrue(new Renaming(Map.of(
                "B", "B"
        ), relationR).isWellDefined(schema));
    }

    @Test
    void isWellDefined_notWellDefinedChild() {
        assertFalse(new Renaming(replacements, new Relation("S")).isWellDefined(schema));
    }

    @Test
    void isWellDefined_nonInjectiveReplacement() {
        assertFalse(new Renaming(Map.of(
                "A", "C",
                "B", "C"
        ), relationR).isWellDefined(schema));
    }

    @Test
    void isWellDefined_mappingToExistingAttribute() {
        assertFalse(new Renaming(Map.of(
                "A", "B"
        ), relationR).isWellDefined(schema));
    }

    @Test
    void isWellDefined_mappingForNonExistingAttribute() {
        assertFalse(new Renaming(Map.of(
                "C", "D"
        ), relationR).isWellDefined(schema));
    }

    @Test
    void isMarked() {
        assertTrue(renaming.isMarked());
    }

    @Test
    void computeSignature() {
        assertEquals(Set.of("C", "D"), renaming.computeSignature(schema));
    }

    @Test
    void computeNullableSignature() {
        assertEquals(Set.of("D"), renaming.computeNullableSignature(schema));
    }

    @Test
    void testClone() {
        assertEquals(renaming, renaming.clone(List.of(relationR)));
    }

    @Test
    void testToString() {
        assert renaming.toString().equals("\u03c1[A->C,B->D]( R )") || renaming.toString().equals("\u03c1[B->D,A->C]( R )");
    }
}