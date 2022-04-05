package coddifier.transformations;

import coddifier.language.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IntersectionReducerTest {

    static IntersectionReducer ir;

    static Relation R;
    static Relation S;
    static Relation T;

    @BeforeAll
    static void setUpAll() {
        ir = new IntersectionReducer();
        R = new Relation("R");
        S = new Relation("S");
        T = new Relation("T");
    }

    @Test
    void transform_simpleIntersectionReduction() {
        var query = new Intersection(R, R);
        assertEquals(R, ir.transform(query));
    }

    @Test
    void transform_simpleIntersectionSimplification() {
        var query = new Intersection(S, R, S);
        assertEquals(new Intersection(R, S), ir.transform(query));
    }

    @Test
    void transform_simpleIntersectionSimplificationAndReduction() {
        var query = new Intersection(R, R, R);
        assertEquals(R, ir.transform(query));
    }

    @Test
    void transform_moreAdvancedReduction() {
        var query = new Intersection(
                new Union(R, S),
                new Union(R, new Intersection(S, S))
        );
        assertEquals(new Union(R, S), ir.transform(query));
    }

    @Test
    void transform_moreAdvancedSimplification() {
        var query = new Intersection(
                new Union(R, new Product(S, new Intersection(T, T, T))),
                R,
                new Union(R, new Product(S, T))
        );
        var equivalentQuery = new Intersection(
                new Union(R, new Product(S, T)),
                R
        );
        assertEquals(equivalentQuery, ir.transform(query));
    }

    @Test
    void transform_noChange() {
        var query =
                new Intersection(
                        new Difference(
                                new Renaming(Map.of("B", "C"), new Union(R, S)),
                                new Renaming(Map.of("D", "A"), new Distinct(T))
                        ),
                        new Projection(Set.of("A", "C"), new Selection(()-> Set.of("A", "B", "C"), new Product(R, T)))
                );

        assertEquals(query, ir.transform(query));
    }
}