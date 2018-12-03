package pfe.terrain.gen;

import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.context.Context;
import pfe.terrain.gen.algo.geometry.Coord;
import pfe.terrain.gen.algo.geometry.Edge;
import pfe.terrain.gen.algo.island.IslandMap;
import pfe.terrain.gen.algo.key.Param;
import pfe.terrain.gen.algo.types.OptionalIntegerType;

import java.util.*;

import static pfe.terrain.gen.RiverGenerator.*;

public class RandomRivers extends Contract {

    public static final Param<Integer> nbRiversParam = new Param<>("nbRivers", Integer.class,
            1, 100, "Number of rivers in the island.", 10, "Amount of rivers");

    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(nbRiversParam);
    }


    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(vertices, seed, edges, faces, vertexWaterKey, heightKey),
                asKeySet(riverFlowKey, isSourceKey, isRiverEndKey)
        );
    }

    @Override
    public void execute(IslandMap map, Context context) {
        RiverGenerator generator = new RiverGenerator(map);
        Random random = new Random(map.getSeed());
        List<Coord> land = new ArrayList<>();
        for (Coord vertex : map.getVertices()) {
            vertex.putProperty(isSourceKey, false);
            vertex.putProperty(isRiverEndKey, false);
        }
        for (Coord vertex : map.getEdgeVertices()) {
            if (!vertex.getProperty(vertexWaterKey).value) {
                land.add(vertex);
            }
        }

        // necessary for deterministic purposes
        land.sort((o1, o2) -> (int) (o1.x + o1.y - o2.x - o2.y));

        for (Edge edge : map.getEdges()) {
            edge.putProperty(riverFlowKey, new OptionalIntegerType(0));
        }
        int nbRivers = context.getParamOrDefault(nbRiversParam);
        for (int i = 0; i < nbRivers; i++) {
            Coord start = land.get(random.nextInt(land.size()));
            while (start.getProperty(isSourceKey)) {
                start = land.get(random.nextInt(land.size()));
            }
            generator.generateRiverFrom(start, new HashSet<>());
        }
    }

}
