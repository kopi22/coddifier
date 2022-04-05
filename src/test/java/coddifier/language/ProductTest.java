package coddifier.language;

import coddifier.db.Attribute;
import coddifier.db.Schema;
import coddifier.db.SimpleSchema;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    static Schema schema;

    static Relation relationR;
    static Relation relationS;
    static Relation relationT;

    @BeforeAll
    static void setUpAll() {
        var sb = new SimpleSchema.Builder();
        sb.addTable("R",
                new Attribute("A", true),
                new Attribute("B", false)
        );
        sb.addTable("S",
                new Attribute("C", false),
                new Attribute("D", true)
        );
        sb.addTable("T",
                new Attribute("B", false),
                new Attribute("C", false)
        );
        sb.addTable("U",
                new Attribute("A", false),
                new Attribute("B", false)
        );
        sb.addTable("V",
                new Attribute("C", false),
                new Attribute("D", false)
        );
        sb.addTable("X",
                new Attribute("A", false),
                new Attribute("B", false),
                new Attribute("C", false),
                new Attribute("D", false)
        );
        schema = sb.build();

        relationR  = new Relation("R");
        relationS  = new Relation("S");
        relationT  = new Relation("T");
    }

    @Test
    void isWellDefined() {
        var product = new Product(relationR, relationS);
        assertTrue(product.isWellDefined(schema));
    }

    @Test
    void isWellDefined_falseLeftChild() {
        var product = new Product(new Relation("Q"), relationS);
        assertFalse(product.isWellDefined(schema));
    }

    @Test
    void isWellDefined_falseRightChild() {
        var product = new Product(relationR, new Relation("Q"));
        assertFalse(product.isWellDefined(schema));
    }

    @Test
    void isWellDefined_falseOverlappingSignature() {
        var product = new Product(relationR, relationT);
        assertFalse(product.isWellDefined(schema));
    }

    @Test
    void satisfiesSufficientConditions_nnaAncestor() {
        var product = new Product(relationR, relationS);
        new Intersection(new Relation("X"), product).isGuaranteedToPreserveCoddSemantics(schema);
        assertTrue(product.nna);
        assertTrue(product.satisfiesSufficientConditions());
    }

    @Test
    void satisfiesSufficientConditions_nnaSelf() {
        var product = new Product(new Relation("U"), new Relation("V"));
        product.isGuaranteedToPreserveCoddSemantics(schema);
        assertTrue(product.nna);
        assertTrue(product.satisfiesSufficientConditions());
    }

    @Test
    void satisfiesSufficientConditions_false() {
        var product = new Product(relationR, relationS);
        assertFalse(product.isGuaranteedToPreserveCoddSemantics(schema));
        assertFalse(product.satisfiesSufficientConditions());
    }

    @Test
    void computeSignature() {
        var product = new Product(relationR, relationS);
        assertEquals(Set.of("A", "B", "C", "D"), product.getSignature(schema));
    }

    @Test
    void computeNullableSignature() {
        var product = new Product(relationR, relationS);
        assertEquals(Set.of("A", "D"), product.getNullableSignature(schema));
    }

    @Test
    void testClone() {
        var productRS = new Product(relationR, relationS);
        var productRV = new Product(relationR, new Relation("V"));
        assertNotEquals(productRS, productRV);
        assertEquals(productRS, productRV.clone(List.of(relationR, relationS)));
    }

    @Test
    void testClone_assertionError() {
        var product = new Product(relationR, relationS);
        assertThrows(AssertionError.class, () -> product.clone(List.of(relationR)));
        assertThrows(AssertionError.class, () -> product.clone(List.of(relationR, relationR, relationR)));
    }

    @Test
    void testToString() {
        var product = new Product(relationR, relationS);
        assertEquals("( R ) X ( S )", product.toString());
    }
}