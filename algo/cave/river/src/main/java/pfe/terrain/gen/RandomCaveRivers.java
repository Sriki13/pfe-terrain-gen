package pfe.terrain.gen;

import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.constraints.key.SerializableKey;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;
import pfe.terrain.gen.algo.types.MarkerType;

import java.util.*;

import static pfe.terrain.gen.RiverGenerator.*;

public class RandomCaveRivers extends Contract {

    public static final Param<Integer> NB_RIVERS_PARAM = new Param<>("nbCaveRivers", Integer.class, 1,
            50, "Number of rivers in the cave.", 10, "Amount of rivers");

    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(NB_RIVERS_PARAM);
    }

    static final Key<BooleanType> VERTEX_WALL_KEY =
            new Key<>(VERTICES_PREFIX + "IS_WALL", BooleanType.class);

    static final Key<DoubleType> HEIGHT_KEY =
            new SerializableKey<>(VERTICES_PREFIX + "CAVE_HEIGHT", "height", DoubleType.class);

    static final Key<MarkerType> FLOOD_KEY = new Key<>("hasFlood", MarkerType.class);

    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(VERTICES, SEED, EDGES, FACES, HEIGHT_KEY, VERTEX_WALL_KEY, FLOOD_KEY),
                asKeySet(RIVER_FLOW_KEY, IS_SOURCE_KEY, IS_RIVER_END_KEY)
        );
    }

    @Override
    public String getDescription() {
        return "Adds a number of sources randomly in the caves, river will flow down from the sources";
    }

    @Override
    public void execute(TerrainMap map, Context context) {
        RiverGenerator generator = new RiverGenerator(map, HEIGHT_KEY);
        Random random = new Random(map.getProperty(SEED));
        List<Coord> empty = new ArrayList<>();

        Set<Coord> edgeVertices = new HashSet<>(map.getProperty(VERTICES));
        map.getProperty(FACES).forEach(face -> edgeVertices.remove(face.getCenter()));
        for (Coord vertex : edgeVertices) {
            if (!vertex.getProperty(VERTEX_WALL_KEY).value
                    && vertex.getProperty(HEIGHT_KEY).value > 0) {
                empty.add(vertex);
            }
        }

        // necessary for deterministic purposes
        empty.sort((o1, o2) -> (int) (1000 * (o1.x + o1.y - o2.x - o2.y)));

        int nbRivers = context.getParamOrDefault(NB_RIVERS_PARAM);
        for (int i = 0; i < nbRivers; i++) {
            Coord start = empty.get(random.nextInt(empty.size()));
            empty.remove(start);
            generator.generateRiverFrom(start, new HashSet<>(),
                    coord -> coord.getProperty(HEIGHT_KEY).value <= 0);
            if (empty.isEmpty()) {
                break;
            }
        }
    }

}
