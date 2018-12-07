package pfe.terrain.gen.algo.road;

import org.jgrapht.graph.DefaultUndirectedGraph;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.OptionalKey;
import pfe.terrain.gen.algo.constraints.key.SerializableKey;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.geometry.*;
import pfe.terrain.gen.algo.types.DoubleType;
import pfe.terrain.gen.algo.types.MarkerType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BridgesBuilder extends Contract {

    private static final Key<DoubleType> VERTEX_HEIGHT_KEY =
            new SerializableKey<>(VERTICES_PREFIX + "HEIGHT", "height", DoubleType.class);

    public static final Key<MarkerType> CITY_KEY =
            new SerializableKey<>(new OptionalKey<>(FACES_PREFIX + "HAS_CITY", MarkerType.class), "isCity");


    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(SIZE, EDGES, VERTICES, FACES, SEED, VERTEX_HEIGHT_KEY, CITY_KEY),
                asKeySet(),
                asKeySet(VERTEX_HEIGHT_KEY)
        );
    }

    @Override
    public String getDescription() {
        return "Creates road between cities based on a fast steiner graph with added edges";
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void execute(TerrainMap terrainMap, Context context) {
        CoordSet coords = terrainMap.getProperty(VERTICES);
        FaceSet faces = terrainMap.getProperty(FACES);
        Set<Coord> edgeCoords = new HashSet<>(coords);
        List<Coord> cities = new ArrayList<>();
        DefaultUndirectedGraph<Coord, Edge> graph = new DefaultUndirectedGraph<>(Edge.class);

        for (Face face : faces) {
            edgeCoords.remove(face.getCenter());
            if (face.hasProperty(CITY_KEY)) {
                for (Coord city : face.getBorderVertices()) {
                    if (city.getProperty(VERTEX_HEIGHT_KEY).value > 0) {
                        cities.add(city);
                        break;
                    }
                }
            }
        }

        for (Coord coord : edgeCoords) {
            if (coord.getProperty(VERTEX_HEIGHT_KEY).value > 0) {
                graph.addVertex(coord);
            }
        }
    }
}
