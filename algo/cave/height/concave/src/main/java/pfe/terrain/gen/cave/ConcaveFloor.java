package pfe.terrain.gen.cave;

import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.constraints.key.SerializableKey;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.island.geometry.Edge;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class ConcaveFloor extends Contract {

    // max = 1 min = 0.5, default = 0.5
    static final Param<Double> VARIATION_PARAM = Param.generateDefaultDoubleParam("caveHeightVariation",
            "Defines the elevation variation of the cave from its borders to the center of its rooms and corridors",
            0.2, "Cave elevation");

    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(VARIATION_PARAM);
    }

    static final Key<BooleanType> FACE_WALL_KEY =
            new Key<>(FACES_PREFIX + "IS_WALL", BooleanType.class);


    static final Key<BooleanType> VERTEX_WALL_KEY =
            new Key<>(VERTICES_PREFIX + "IS_WALL", BooleanType.class);

    static final Key<DoubleType> HEIGHT_KEY =
            new SerializableKey<>(VERTICES_PREFIX + "CAVE_HEIGHT", "height", DoubleType.class);

    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(FACES, VERTICES, VERTEX_WALL_KEY, SIZE, FACE_WALL_KEY),
                asKeySet(),
                asKeySet(HEIGHT_KEY)
        );
    }

    @Override
    public String getDescription() {
        return "Makes the floor take a concave shape";
    }


    @Override
    public void execute(TerrainMap map, Context context) {
        Set<Coord> allEmptyCoords = getEmptyCoords(map);
        Set<Coord> processedCoords = new HashSet<>();
        Set<Coord> coordsToProcess = getCoordsNextToWalls(allEmptyCoords, map);
        Set<Edge> emptyEdges = getEmptyEdges(map);

        double hardness = 0.5 * (context.getParamOrDefault(VARIATION_PARAM)) + 0.5;
        Random random = new Random(map.getProperty(SEED));
        double step = heightStep(hardness, random);

        double attenuation = 1;
        double factor = 1;
        if (map.getProperty(SIZE) >= 8000) {
            factor = 0.95;
        }

        while (processedCoords.size() < allEmptyCoords.size()) {
            for (Coord vertex : coordsToProcess) {
                vertex.putProperty(HEIGHT_KEY, new DoubleType(vertex.getProperty(HEIGHT_KEY).value - step));
            }
            processedCoords.addAll(coordsToProcess);
            coordsToProcess = getNextCoordSet(emptyEdges, coordsToProcess);
            step += heightStep(hardness, random) * attenuation;
            attenuation *= factor;
        }
        map.getProperty(FACES).stream()
                .filter(face -> !face.getProperty(FACE_WALL_KEY).value)
                .forEach(face -> {
                    double avg = 0;
                    for (Coord vertex : face.getBorderVertices()) {
                        avg += vertex.getProperty(HEIGHT_KEY).value;
                    }
                    face.getCenter().putProperty(HEIGHT_KEY, new DoubleType(avg / face.getBorderVertices().size()));
                });
    }

    private Set<Coord> getEmptyCoords(TerrainMap map) {
        Set<Coord> result = map.getProperty(VERTICES).stream()
                .filter(v -> !v.getProperty(VERTEX_WALL_KEY).value)
                .collect(Collectors.toSet());
        map.getProperty(FACES).forEach(face -> result.remove(face.getCenter()));
        return result;
    }

    private Set<Edge> getEmptyEdges(TerrainMap map) {
        return map.getProperty(EDGES).stream()
                .filter(edge -> !edge.getStart().getProperty(VERTEX_WALL_KEY).value
                        && !edge.getEnd().getProperty(VERTEX_WALL_KEY).value)
                .collect(Collectors.toSet());
    }

    private Set<Coord> getCoordsNextToWalls(Set<Coord> candidates, TerrainMap map) {
        Set<Coord> result = new HashSet<>();
        for (Edge edge : map.getProperty(EDGES)) {
            if (candidates.contains(edge.getStart()) && edge.getEnd().getProperty(VERTEX_WALL_KEY).value) {
                result.add(edge.getStart());
            } else if (candidates.contains(edge.getEnd()) && edge.getStart().getProperty(VERTEX_WALL_KEY).value) {
                result.add(edge.getEnd());
            }
        }
        return result;
    }

    private Set<Coord> getNextCoordSet(Set<Edge> edges, Set<Coord> lastProcessed) {
        Set<Coord> result = new HashSet<>();
        Set<Edge> processedEdges = new HashSet<>();
        for (Edge edge : edges) {
            if (lastProcessed.contains(edge.getStart()) && !lastProcessed.contains(edge.getEnd())) {
                result.add(edge.getEnd());
                processedEdges.add(edge);
            } else if (lastProcessed.contains(edge.getEnd()) && !lastProcessed.contains(edge.getStart())) {
                result.add(edge.getStart());
                processedEdges.add(edge);
            }
        }
        edges.removeAll(processedEdges);
        return result;
    }

    private double heightStep(double hardness, Random random) {
        return random.nextDouble() * 10 * hardness;
    }

}
