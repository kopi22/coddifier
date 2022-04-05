package coddifier;

import coddifier.db.Attribute;
import coddifier.db.SchemaException;
import coddifier.db.SimpleSchema;
import coddifier.language.*;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CoddifierTest {

    @Test
    void preservesCoddAsIs() {
        var schema = new SimpleSchema.Builder()
                .addTable("R", new Attribute("A"))
                .addTable("S", new Attribute("A", false))
                .addTable("T", new Attribute("A", false))
                .build();
        var R = new Relation("R");
        var S = new Relation("S");
        var T = new Relation("T");
        var expression = new Intersection(R, new Intersection(S, T));

        assertTrue(Coddifier.isGuaranteedToPreserveCoddSemanticsAsIs(expression, schema));
        assertTrue(Coddifier.isGuaranteedToPreserveCoddSemantics(expression, schema));
    }

    @Test
    void preservesCoddTransformed_merge() {
        var schema = new SimpleSchema.Builder()
                .addTable("R", new Attribute("A", false), new Attribute("B"), new Attribute("C"))
                .addTable("S", new Attribute("A"), new Attribute("B", false), new Attribute("C"))
                .addTable("T", new Attribute("A"), new Attribute("B"), new Attribute("C", false))
                .build();
        var R = new Relation("R");
        var S = new Relation("S");
        var T = new Relation("T");
        var expression = new Intersection(R, new Intersection(S, T));

        assertFalse(Coddifier.isGuaranteedToPreserveCoddSemanticsAsIs(expression, schema));
        assertTrue(Coddifier.isGuaranteedToPreserveCoddSemantics(expression, schema));
    }

    @Test
    void preservesCoddTransformed_reduction() {
        var schema = new SimpleSchema.Builder()
                .addTable("R", new Attribute("A"))
                .build();
        var R = new Relation("R");
        var expression = new Intersection(R, R);

        assertFalse(Coddifier.isGuaranteedToPreserveCoddSemanticsAsIs(expression, schema));
        assertTrue(Coddifier.isGuaranteedToPreserveCoddSemantics(expression, schema));
    }

    @Test
    void preservesCoddTransformed_propagation() {
        var schema = new SimpleSchema.Builder()
                .addTable("R", new Attribute("A"), new Attribute("B", false))
                .addTable("S", new Attribute("A"), new Attribute("B"))
                .build();
        var R = new Relation("R");
        var S = new Relation("S");

        var condition = new Condition() {
            @Override
            public Set<String> getConstantSignature() {
                return Set.of("A");
            }

            @Override
            public Set<String> getSignature() {
                return Set.of("A");
            }

            @Override
            public String toString() {
                return "A=1";
            }
        };

        var expression = new Selection(condition, new Intersection(R, S));

        assertFalse(Coddifier.isGuaranteedToPreserveCoddSemanticsAsIs(expression, schema));
        assertTrue(Coddifier.isGuaranteedToPreserveCoddSemantics(expression, schema));
    }

    @Test
    void notGuaranteedToPreserveCodd() {
        var schema = new SimpleSchema.Builder()
                .addTable("R", new Attribute("A"), new Attribute("B"), new Attribute("C"))
                .addTable("S", new Attribute("A"), new Attribute("B", false), new Attribute("C"))
                .addTable("T", new Attribute("A"), new Attribute("B"), new Attribute("C", false))
                .build();
        var R = new Relation("R");
        var S = new Relation("S");
        var T = new Relation("T");
        var expression = new Intersection(R, new Intersection(S, T));

        assertFalse(Coddifier.isGuaranteedToPreserveCoddSemanticsAsIs(expression, schema));
        assertFalse(Coddifier.isGuaranteedToPreserveCoddSemantics(expression, schema));
    }

    @Test
    void isGuaranteedToPreserveCoddSemantics_throwsOnNotWellDefined() {
        var schema = new SimpleSchema.Builder().build();
        var expression = new Distinct(new Relation("B"));
        assertThrows(SchemaException.class, () -> Coddifier.isGuaranteedToPreserveCoddSemantics(expression, schema));
    }

    @Test
    void isGuaranteedToPreserveCoddSemanticsAsIs_throwsOnNotWellDefined() {
        var schema = new SimpleSchema.Builder().build();
        var expression = new Distinct(new Relation("B"));
        assertThrows(SchemaException.class, () -> Coddifier.isGuaranteedToPreserveCoddSemanticsAsIs(expression, schema));
    }

}