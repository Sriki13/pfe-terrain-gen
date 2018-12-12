package pfe.terrain.gen.cave;

import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
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

import java.util.*;
import java.util.stream.Collectors;

public class CaveLinker extends Contract {

    static final Param<String> BIG_TUNNELS = new Param<>("bigTunnels", String.class, "true or false",
            "Use big tunnels instead of narrow ones",
            "false", "Wide tunnels");

    static final Param<Double> MINSIZE_LINK = Param.generateDefaultDoubleParam("minSizeForLink",
            "How big a cave must be in order to be linked to the rest (0 = small, 1 = huge)", 0.2, "Minimum link for size");

    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(BIG_TUNNELS, MINSIZE_LINK);
    }

    private static final Key<BooleanType> FACE_WALL_KEY =
            new SerializableKey<>(FACES_PREFIX + "IS_WALL", "isWall", BooleanType.class);

    private static final Key<BooleanType> VERTEX_WALL_KEY =
            new SerializableKey<>(VERTICES_PREFIX + "IS_WALL", "isWall", BooleanType.class);

    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(FACES, SEED, EDGES, VERTICES),
                asKeySet(),
                asKeySet(FACE_WALL_KEY, VERTEX_WALL_KEY));
    }

    @Override
    public String getDescription() {
        return "Links all caves bigger than a threshold";
    }

    @Override
    public void execute(TerrainMap map, Context context) {
        boolean bigTunnels = Boolean.parseBoolean(context.getParamOrDefault(BIG_TUNNELS));
        int minSizeLink = (int) ((context.getParamOrDefault(MINSIZE_LINK) + 0.1) * 50);
        DefaultUndirectedGraph<Coord, Edge> graph = GraphLib.buildCaveGraph(map);
        ConnectivityInspector<Coord, Edge> connectivityInspector = new ConnectivityInspector<>(graph);

        List<Set<Coord>> caves = connectivityInspector.connectedSets();
        caves = caves.stream().filter(cset -> cset.size() > minSizeLink).collect(Collectors.toList());
        caves.sort(Comparator.comparingInt(Set::size));
        Set<Coord> sourceCave = caves.get(caves.size() - 1);

        DefaultUndirectedGraph<Coord, Edge> allNodeGraph = GraphLib.buildFullGraphRandomWeight(map, new Random(map.getProperty(SEED)));

        Set<Edge> path = new HashSet<>();
        for (int i = 0; i < caves.size() - 1; i++) {
            path.addAll(findPath(sourceCave.stream().findAny().get(), caves.get(i).stream().findAny().get(), allNodeGraph));
        }
        for (Face face : map.getProperty(FACES)) {
            if (face.getProperty(FACE_WALL_KEY).value) {
                for (Edge e : path) {
                    if (face.getEdges().contains(e)) {
                        setFaceEmpty(face, new BooleanType(false));
                        if (bigTunnels) {
                            for (Face neighbor : face.getNeighbors()) {
                                setFaceEmpty(neighbor, new BooleanType(false));
                            }
                        }
                        break;
                    }
                }
            }
        }
    }

    private void setFaceEmpty(Face face, BooleanType value) {
        face.putProperty(FACE_WALL_KEY, value);
        face.getBorderVertices().forEach(c -> c.putProperty(VERTEX_WALL_KEY, value));
        face.getCenter().putProperty(VERTEX_WALL_KEY, value);
    }

    private List<Edge> findPath(Coord source, Coord target, DefaultUndirectedGraph<Coord, Edge> allNodeGraph) {
        DijkstraShortestPath<Coord, Edge> path = new DijkstraShortestPath<>(allNodeGraph);
        return path.getPath(source, target).getEdgeList();
    }
}
