import org.junit.Test;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.types.IntegerType;
import pfe.terrain.generatorService.GraphGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class GraphGeneratorTest {

    private GraphGenerator graphGenerator;

    @Test
    public void graphTest() throws Exception {
        graphGenerator = new GraphGenerator(Arrays.asList(
                new TestContract("init", Arrays.asList(
                        new Key<>("SIZE", IntegerType.class),
                        new Key<>("SEED", IntegerType.class)),
                        new ArrayList<>()
                ),
                new TestContract("pointsGen", Collections.singletonList(
                        new Key<>("POINTS", IntegerType.class)),
                        Arrays.asList(
                                new Key<>("SIZE", IntegerType.class),
                                new Key<>("SEED", IntegerType.class)
                        )
                ),
                new TestContract("mesh",
                        Arrays.asList(
                                new Key<>("VERTICES", IntegerType.class),
                                new Key<>("EDGES", IntegerType.class),
                                new Key<>("FACES", IntegerType.class)
                        ),
                        Arrays.asList(
                                new Key<>("SIZE", IntegerType.class),
                                new Key<>("SEED", IntegerType.class),
                                new Key<>("POINTS", IntegerType.class))
                ),
                new TestContract("border", Collections.singletonList(
                        new Key<>("BORDER", IntegerType.class)),
                        Arrays.asList(
                                new Key<>("VERTICES", IntegerType.class),
                                new Key<>("EDGES", IntegerType.class),
                                new Key<>("FACES", IntegerType.class)
                        )
                )));
        graphGenerator.generateGraph();
        graphGenerator.exportAsPNG("test.png");
    }

}
