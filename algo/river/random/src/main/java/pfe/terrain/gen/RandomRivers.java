package pfe.terrain.gen;

import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.constraints.key.SerializableKey;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.types.DoubleType;

import java.util.*;

import static pfe.terrain.gen.RiverGenerator.*;

public class RandomRivers extends Contract {

    public static final Param<Integer> NB_RIVERS_PARAM = Param.generatePositiveIntegerParam("nbRivers",
            100, "Number of rivers in the island.", 10, "Amount of rivers");

    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(NB_RIVERS_PARAM);
    }

    public static final Key<DoubleType> HEIGHT_KEY =
            new SerializableKey<>(VERTICES_PREFIX + "HEIGHT", "height", DoubleType.class);

    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(VERTICES, SEED, EDGES, FACES, VERTEX_WATER_KEY, HEIGHT_KEY),
                asKeySet(RIVER_FLOW_KEY, IS_SOURCE_KEY, IS_RIVER_END_KEY)
        );
    }

    @Override
    public String getDescription() {
        return "Adds a number of sources randomly on emerged land, river will flow down from the sources";
    }

    @Override
    public void execute(TerrainMap map, Context context) {
        RiverGenerator generator = new RiverGenerator(map, HEIGHT_KEY);
        Random random = new Random(map.getProperty(SEED));
        List<Coord> land = new ArrayList<>();
        Set<Coord> edgeVertices = new HashSet<>(map.getProperty(VERTICES));
        map.getProperty(FACES).forEach(face -> edgeVertices.remove(face.getCenter()));
        for (Coord vertex : edgeVertices) {
            if (!vertex.getProperty(VERTEX_WATER_KEY).value) {
                land.add(vertex);
            }
        }

        // necessary for deterministic purposes
        land.sort((o1, o2) -> (int) (1000 *(o1.x + o1.y - o2.x - o2.y)));

        int nbRivers = context.getParamOrDefault(NB_RIVERS_PARAM);
        for (int i = 0; i < nbRivers; i++) {
            Coord start = land.get(random.nextInt(land.size()));
            while (start.getProperty(IS_SOURCE_KEY) != null) {
                start = land.get(random.nextInt(land.size()));
            }
            generator.generateRiverFrom(start, new HashSet<>());
        }
    }

}
