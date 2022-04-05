package coddifier.transformations;

import coddifier.language.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IntersectionCombinerTest {

    static IntersectionCombiner ic;

    @BeforeAll
    static void setUpAll() {
        ic = new IntersectionCombiner();
    }

    @Test
    void transform_simpleChainOfIntersections() {
        var R = new Relation("R");
        var S = new Relation("S");
        var T = new Relation("T");

        var query =
            new Intersection(
                new Intersection(
                    R,
                    S
                ),
                T
            );

        var equivalentQuery = new Intersection(R, S, T);

        assertEquals(equivalentQuery, ic.transform(query));
    }

    @Test
    void transform_parallelChainsOfIntersection() {
        var R = new Relation("R");
        var S = new Relation("S");
        var T = new Relation("T");

        var query =
            new Union(
                new Intersection(
                    new Intersection(
                        R,
                        R
                    ),
                    new Intersection(
                        S,
                        S
                    )
                ),
                new Intersection(
                    new Intersection(
                        R,
                        S
                    ),
                    T
                )
            );

        var equivalentQuery = new Union(
                new Intersection(R, R, S, S),
                new Intersection(R, S, T)
        );

        assertEquals(equivalentQuery, ic.transform(query));
    }

    @Test
    void transform_disjointChainsOfIntersections() {
        var R = new Relation("R");
        var S = new Relation("S");

        var query =
            new Intersection(
                new Intersection(
                    new Union(
                        new Intersection(
                            new Intersection(
                                R,
                                R
                            ),
                            R
                        ),
                        S
                    ),
                    R
                ),
                R
            );

        var equivalentQuery = new Intersection(
            new Union(
                new Intersection(R, R, R),
                S
            ),
            R,
            R
        );

        assertEquals(equivalentQuery, ic.transform(query));
    }

    @Test
    void transform_noChange() {
        var R = new Relation("R");
        var S = new Relation("S");
        var T = new Relation("T");

        var query =
                new Intersection(
                        new Difference(
                                new Renaming(Map.of("B", "C"), new Union(R, S)),
                                new Renaming(Map.of("D", "A"), new Distinct(T))
                        ),
                        new Projection(Set.of("A", "C"), new Selection(()-> Set.of("A", "B", "C"), new Product(R, T)))
                );

        assertEquals(query, ic.transform(query));
    }
}