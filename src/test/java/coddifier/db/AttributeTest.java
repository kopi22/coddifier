package coddifier.db;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AttributeTest {

    @Test
    void getName() {
        var A = new Attribute("A");
        assertEquals("A", A.getName());
    }

    @Test
    void getIsNullable_trueImplicit() {
        var A = new Attribute("A");
        assertTrue(A.getIsNullable());
    }

    @Test
    void getIsNullable_trueExplicit() {
        var A = new Attribute("A", true);
        assertTrue(A.getIsNullable());
    }

    @Test
    void getIsNullable_false() {
        var A = new Attribute("A", false);
        assertFalse(A.getIsNullable());
    }

    @Test
    void testEquals_true() {
        var A1 = new Attribute("A");
        var A2 = new Attribute("A", true);
        assertEquals(A1, A1);
        assertEquals(A1, A2);
    }

    @Test
    void testEquals_false_differentNullability() {
        var A_null = new Attribute("A");
        var A_nonnull = new Attribute("A", false);
        assertNotEquals(A_null, A_nonnull);
    }

    @Test
    void testEquals_false_differentNames() {
        var A = new Attribute("A");
        var B = new Attribute("B");
        assertNotEquals(A, B);
    }

    // for the sake of completness - to cover a branch in equals method
    @Test
    void testEquals_notAttribute() {
        var A = new Attribute("A");
        assertNotEquals(A, "A");
    }

    @Test
    void testHashCode() {
        var A1 = new Attribute("A");
        var A2 = new Attribute("A", true);
        assertEquals(A1.hashCode(), A2.hashCode());
    }
}