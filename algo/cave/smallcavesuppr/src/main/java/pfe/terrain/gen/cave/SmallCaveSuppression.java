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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SmallCaveSuppression extends Contract {

    static final Key<BooleanType> FACE_WALL_KEY =
            new SerializableKey<>(FACES_PREFIX + "IS_WALL", "isWall", BooleanType.class);

    static final Key<BooleanType> VERTEX_WALL_KEY =
            new SerializableKey<>(VERTICES_PREFIX + "IS_WALL", "isWall", BooleanType.class);

    static final Param<Double> SUPPRESSION_PERCENTAGE = Param.generateDefaultDoubleParam("smallCaveSuppressionPercentage",
            "How many of the small caves to delete, a very small portion (0.0) or all except one (1.0)", 0.5, "Small caves suppression percentage");

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
        DefaultUndirectedGraph<Coord, Edge> graph = new DefaultUndirectedGraph<Coord, Edge>(Edge.class);
        ConnectivityInspector<Coord, Edge> connectivityInspector = new ConnectivityInspector<>(graph);
        Set<Coord> edgeCoords = new HashSet<>(map.getProperty(VERTICES));

        for (Face face : map.getProperty(FACES)) {
            edgeCoords.remove(face.getCenter());
        }

        for (Coord coord : edgeCoords) {
            if (!coord.getProperty(VERTEX_WALL_KEY).value) {
                graph.addVertex(coord);
            }
        }

        for (Edge edge : map.getProperty(EDGES)) {
            if (!edge.getStart().getProperty(VERTEX_WALL_KEY).value && !edge.getEnd().getProperty(VERTEX_WALL_KEY).value) {
                graph.addEdge(edge.getStart(), edge.getEnd(), edge);
            }
        }

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
            for (Coord c : face.getBorderVertices()) {
                if (!c.getProperty(VERTEX_WALL_KEY).value) {
                    break;
                }
            }
            face.putProperty(FACE_WALL_KEY, new BooleanType(true));
            face.getCenter().putProperty(FACE_WALL_KEY, new BooleanType(true));
        }
    }
}
