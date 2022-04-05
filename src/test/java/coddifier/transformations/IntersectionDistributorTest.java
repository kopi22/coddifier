package coddifier.transformations;

import coddifier.language.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IntersectionDistributorTest {

    static IntersectionDistributor id;

    static Relation R;
    static Condition condition;
    static Map<String, String> renamings;

    static Selection templateSelection;
    static Renaming templateRenaming;

    @BeforeAll
    static void setUpAll() {
        id = new IntersectionDistributor();

        R = new Relation("R");
        condition = () -> null;
        renamings = new HashMap<>();

        templateSelection = new Selection(condition, R);
        templateRenaming = new Renaming(renamings, R);
    }

    private static Stream<Expression> provideOps() {
        return Stream.of(templateRenaming, templateSelection);
    }

    @ParameterizedTest
    @MethodSource("provideOps")
    void transform_simplePropagationOfRenSel(Expression opTemplate) {
        var expression = opTemplate.clone(List.of(new Intersection(R, R)));

        var expected = new Intersection(
                opTemplate.clone(List.of(R)),
                opTemplate.clone(List.of(R))
        );
        assertEquals(expected, id.transform(expression));
    }

    @ParameterizedTest
    @MethodSource("provideOps")
    void transform_skipNonOpPropagationOfRenSel(Expression opTemplate) {
        var expression = new Distinct(opTemplate.clone(List.of(new Intersection(R, R))));

        var expected = new Distinct(
                new Intersection(
                    opTemplate.clone(List.of(R)),
                    opTemplate.clone(List.of(R))
                )
        );
        assertEquals(expected, id.transform(expression));
    }

    @ParameterizedTest
    @MethodSource("provideOps")
    void transform_nestedOpPropagationOfRenSel(Expression opTemplate) {
        var expression = opTemplate.clone(List.of(opTemplate.clone(List.of(new Intersection(R, R)))));

        var expected = new Intersection(
                opTemplate.clone(List.of(opTemplate.clone(List.of(R)))),
                opTemplate.clone(List.of(opTemplate.clone(List.of(R))))
        );
        assertEquals(expected, id.transform(expression));
    }

    @ParameterizedTest
    @MethodSource("provideOps")
    void transform_nestedOpNoPropagationOfRenSel(Expression opTemplate) {
        var expression = opTemplate.clone(List.of(opTemplate.clone(List.of(R))));
        assertEquals(expression, id.transform(expression));
    }

    @ParameterizedTest
    @MethodSource("provideOps")
    void transform_parallelPropagationOfRenSel(Expression opTemplate) {
        var expression = new Intersection(
                opTemplate.clone(List.of(
                        new Intersection(
                                R,
                                R
                        )
                )),
                opTemplate.clone(List.of(
                        new Intersection(
                                R,
                                R
                        )
                ))
        );

        var expected = new Intersection(
                new Intersection(
                        opTemplate.clone(List.of(R)),
                        opTemplate.clone(List.of(R))
                ),
                new Intersection(
                        opTemplate.clone(List.of(R)),
                        opTemplate.clone(List.of(R))
                )
        );
        assertEquals(expected, id.transform(expression));
    }

    @ParameterizedTest
    @MethodSource("provideOps")
    void transform_disconnectedPropagationOfRenSel(Expression opTemplate) {
        var expression = opTemplate.clone(List.of(
                new Intersection(
                        R,
                        new Product(
                                R,
                                opTemplate.clone(List.of(
                                        new Intersection(
                                                R,
                                                R
                                        )
                                ))
                        )
                )
        ));

        var expected = new Intersection(
                opTemplate.clone(List.of(
                        R
                )),
                opTemplate.clone(List.of(
                        new Product(
                                R,
                                new Intersection(
                                        opTemplate.clone(List.of(R)),
                                        opTemplate.clone(List.of(R))
                                )
                        )
                ))
        );

        assertEquals(expected, id.transform(expression));
    }
}