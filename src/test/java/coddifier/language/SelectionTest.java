package coddifier.language;

import coddifier.db.Attribute;
import coddifier.db.Schema;
import coddifier.db.SimpleSchema;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SelectionTest {

    static Schema schema;
    static Relation relationR;

    static Condition condition;
    static Condition conditionNotWellDefined;

    Selection selection;

    @BeforeAll
    static void setUpAll() {
        var sb = new SimpleSchema.Builder();
        sb.addTable("R", Set.of(
                new Attribute("A", true),
                new Attribute("B", true)
        ));
        sb.addTable("S", Set.of(
                new Attribute("A", true),
                new Attribute("B", true)
        ));
        schema = sb.build();

        relationR  = new Relation("R");

        condition = new Condition() {
            @Override
            public Set<String> getSignature() {
                return Set.of("A", "B");
            }

            @Override
            public Set<String> getConstantSignature() {
                return Set.of("B");
            }

            @Override
            public String toString() {
                return "A = B";
            }
        };

        conditionNotWellDefined = new Condition() {
            @Override
            public Set<String> getSignature() {
                return Set.of("A", "C");
            }
        };
    }

    @BeforeEach
    void setUp() {
        selection = new Selection(condition, relationR);
    }

    @Test
    void isWellDefined() {

    }

    @Test
    void isMarked() {
        assertTrue(selection.satisfiesSufficientConditions());
    }

    @Test
    void computeSignature() {
        assertEquals(Set.of("A", "B"), selection.computeSignature(schema));
    }

    @Test
    void computeNullableSignature_1() {
        assertEquals(Set.of("A"), selection.computeNullableSignature(schema));
    }

    @Test
    void computeNullableSignature_2() {
        assertEquals(Set.of("A", "B"), new Selection(Collections::emptySet, relationR).computeNullableSignature(schema));
    }

    @Test
    void testClone() {
        var relationS = new Relation(("S"));
        assertEquals(selection.clone(List.of(relationS)), new Selection(condition, relationS));
    }

    @Test
    void testClone_wrongChildren() {
        assertThrows(AssertionError.class, () -> selection.clone(Collections.emptyList()));
    }

    @Test
    void testToString() {
        assertEquals("\u03C3[A = B]( R )", selection.toString());
    }
}