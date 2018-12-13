package pfe.terrain.gen.cave;

import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultUndirectedGraph;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.constraints.key.SerializableKey;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.island.geometry.Edge;
import pfe.terrain.gen.algo.island.geometry.Face;
import pfe.terrain.gen.algo.types.BooleanType;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class SmallCaveSuppression extends Contract {

    private static final Key<BooleanType> FACE_WALL_KEY =
            new SerializableKey<>(FACES_PREFIX + "IS_WALL", "isWall", BooleanType.class);

    private static final Key<BooleanType> VERTEX_WALL_KEY =
            new SerializableKey<>(VERTICES_PREFIX + "IS_WALL", "isWall", BooleanType.class);

    private static final Param<Double> SUPPRESSION_PERCENTAGE = Param.generateDefaultDoubleParam("smallCaveSuppressionPercentage",
            "How many of the small caves to delete, a very small portion (0.0) or all except one (1.0)", 0.7, "Small caves suppression percentage");

    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(SUPPRESSION_PERCENTAGE);
    }

    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(FACES, SEED, EDGES, VERTICES),
                asKeySet(),
                asKeySet(FACE_WALL_KEY, VERTEX_WALL_KEY));
    }

    @Override
    public String getDescription() {
        return "Removes small caves that are not connected";
    }

    @Override
    public void execute(TerrainMap map, Context context) {
        double supprPercentage = context.getParamOrDefault(SUPPRESSION_PERCENTAGE);
        DefaultUndirectedGraph<Coord, Edge> graph = GraphLib.buildCaveGraph(map);
        ConnectivityInspector<Coord, Edge> connectivityInspector = new ConnectivityInspector<>(graph);

        List<Set<Coord>> caves = connectivityInspector.connectedSets();
        caves.sort(Comparator.comparingInt(Set::size));

        int nbToDelete = (int) Math.ceil((caves.size() - 1) * supprPercentage + 0.01);
        if (nbToDelete > caves.size() - 1) {
            nbToDelete = caves.size() - 1;
        }

        for (int i = 0; i < nbToDelete; i++) {
            caves.get(i).forEach(c -> c.putProperty(VERTEX_WALL_KEY, new BooleanType(true)));
        }

        for (Face face : map.getProperty(FACES)) {
            boolean anyEmpty = face.getBorderVertices().stream()
                    .anyMatch(c -> !c.getProperty(VERTEX_WALL_KEY).value);
            if (!anyEmpty) {
                face.getCenter().putProperty(VERTEX_WALL_KEY, new BooleanType(true));
                face.putProperty(FACE_WALL_KEY, new BooleanType(true));
            }
        }
    }
}
