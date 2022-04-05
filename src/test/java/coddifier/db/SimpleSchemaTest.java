package coddifier.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SimpleSchemaTest {

    SimpleSchema schema;

    @BeforeEach
    void setUp() {
        schema = new SimpleSchema.Builder()
                .addTable("R", new Attribute("A", true), new Attribute("B", true))
                .addTable("S", new Attribute("A", false), new Attribute("C", true))
                .build();
    }

    @Test
    void getRelationAttributeNames_throwsOnMissingRelation() {
        assertThrows(SchemaException.class, () -> schema.getRelationAttributeNames("T"));
    }

    @Test
    void getRelationAttributeNames() {
        assertEquals(Set.of("A", "B"), schema.getRelationNullableAttributeNames("R"));
    }

    @Test
    void getRelationNullableAttributeNames_throwsOnMissingRelation() {
        assertThrows(SchemaException.class, () -> schema.getRelationNullableAttributeNames("T"));
    }

    @Test
    void getRelationNullableAttributeNames() {
        assertEquals(Set.of("C"), schema.getRelationNullableAttributeNames("S"));
    }

    @Test
    void hasRelation_true() {
        assertTrue(schema.hasRelation("R"));
    }

    @Test
    void hasRelation_false() {
        assertFalse(schema.hasRelation("T"));
    }

    @Test
    void testEquals_true() {
        var identicalSchema = new SimpleSchema.Builder()
                .addTable("S", new Attribute("A", false), new Attribute("C", true))
                .addTable("R", new Attribute("A", true), new Attribute("B", true))
                .build();
        assertEquals(schema, identicalSchema);
    }

    @Test
    void testEquals_falseDifferentRelations() {
        var differentSchema = new SimpleSchema.Builder()
                .addTable("S", new Attribute("A", false), new Attribute("C", true))
                .addTable("T", new Attribute("A", true), new Attribute("B", true))
                .build();
        assertNotEquals(schema, differentSchema);
    }

    @Test
    void testEquals_falseEmptySchema() {
        var differentSchema = new SimpleSchema.Builder().build();
        assertNotEquals(schema, differentSchema);
    }

    @Test
    void testEquals_falseDifferentAttributes() {
        var differentSchema = new SimpleSchema.Builder()
                .addTable("S", new Attribute("A", true), new Attribute("C", true))
                .addTable("T", new Attribute("A", true), new Attribute("B", true))
                .build();
        assertNotEquals(schema, differentSchema);
    }

    @Test
    void testHashCode() {
        var identicalSchema = new SimpleSchema.Builder()
                .addTable("S", new Attribute("A", false), new Attribute("C", true))
                .addTable("R", new Attribute("A", true), new Attribute("B", true))
                .build();
        assertEquals(schema.hashCode(), identicalSchema.hashCode());
    }
}