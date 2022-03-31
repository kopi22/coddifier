package coddifier.language;

import coddifier.db.Attribute;
import coddifier.db.Schema;
import coddifier.db.SimpleSchema;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UnionTest {

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
        assertTrue(new Union(relationR, relationS).isWellDefined(schema));
    }

    @Test
    void isWellDefined_leftOperandNotWellDefined() {
        assertFalse(new Union(new Relation("P"), relationS).isWellDefined(schema));
    }

    @Test
    void isWellDefined_rightOperandNotWellDefined() {
        assertFalse(new Union(relationR, new Relation("P")).isWellDefined(schema));
    }

    @Test
    void isWellDefined_sigNotMatch() {
        assertFalse(new Union(relationR, new Relation("U")).isWellDefined(schema));
    }

    @Test
    void isMarked_djb() {
        var union = new Union(relationR, relationS);
        assertTrue(union.satisfiesDJB());
        assertTrue(union.satisfiesSufficientConditions());
    }

    @Test
    void isMarked_nnc() {
        var union = new Union(relationR, relationT);
        assertTrue(union.satisfiesNNC(schema));
        assertTrue(union.satisfiesSufficientConditions());
    }

    @Test
    void isMarked_nna_ancestor() {
        var union = new Union(relationR, relationS);
        assertTrue(union.satisfiesNNA(schema, true));
        assertTrue(union.satisfiesSufficientConditions());
    }

    @Test
    void isMarked_nna_self() {
        var union = new Union(relationT, relationT);
        assertTrue(union.satisfiesNNA(schema, false));
        assertTrue(union.satisfiesSufficientConditions());
    }

    @Test
    void isMarked_false() {
        var union = new Union(relationR, relationR);
        union.isGuaranteedToPreserveCoddSemantics(schema);
        assertFalse(union.satisfiesSufficientConditions());
    }

    @Test
    void computeSignature() {
        var union = new Union(relationR, relationR);
        assertEquals(Set.of("A", "B"), union.computeSignature(schema));
    }

    @Test
    void computeNullableSignature_1() {
        var union = new Union(relationR, relationS);
        assertEquals(Set.of("A", "B"), union.computeNullableSignature(schema));
    }

    @Test
    void computeNullableSignature_2() {
        var union = new Union(relationR, relationT);
        assertEquals(Set.of("A"), union.computeNullableSignature(schema));
    }

    @Test
    void testClone() {
        var unionRT = new Union(relationR, relationT);
        var unionRS = new Union(relationR, relationS);
        assertEquals(unionRT, unionRS.clone(List.of(relationR, relationT)));
    }

    @Test
    void testClone_wrongChildren() {
        var unionRS = new Union(relationR, relationS);
        assertThrows(AssertionError.class, () -> unionRS.clone(List.of(relationR)));
    }

    @Test
    void testToString() {
        assertEquals("( R ) \u222A ( S )", new Union(relationR, relationS).toString());
    }
}