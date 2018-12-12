package pfe.terrain.gen;

import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.constraints.key.SerializableKey;
import pfe.terrain.gen.algo.island.Biome;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.WaterKind;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.island.geometry.Face;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;
import pfe.terrain.gen.algo.types.MarkerType;
import pfe.terrain.gen.algo.types.OptionalIntegerType;

import java.util.*;
import java.util.stream.Collectors;

import static pfe.terrain.gen.RiverGenerator.*;

public class LakesFromRivers extends Contract {

    public static final Param<Integer> LAKE_SIZE_PARAM =
            Param.generatePositiveIntegerParam("lakesLimit", 10,
                    "The limit for the size of the lakes.", 4, "Lake size limit");

    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(LAKE_SIZE_PARAM);
    }

    public static final Key<DoubleType> HEIGHT_KEY =
            new SerializableKey<>(VERTICES_PREFIX + "HEIGHT", "height", DoubleType.class);

    public static final Key<Biome> FACE_BIOME_KEY =
            new SerializableKey<>(FACES_PREFIX + "BIOME", "biome", Biome.class);


    // Produced

    public static final Key<MarkerType> HAS_LAKES_KEY = new Key<>("LAKES", MarkerType.class);

    // Modified

    public static final Key<BooleanType> FACE_WATER_KEY =
            new SerializableKey<>(FACES_PREFIX + "IS_WATER", "isWater", BooleanType.class);

    public static final Key<WaterKind> WATER_KIND_KEY =
            new SerializableKey<>(FACES_PREFIX + "WATER_KIND", "waterKind", WaterKind.class);


    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(VERTICES, SEED, EDGES, FACES),
                asKeySet(HAS_LAKES_KEY),
                asKeySet(VERTEX_WATER_KEY, HEIGHT_KEY, FACE_WATER_KEY, WATER_KIND_KEY,
                        RIVER_FLOW_KEY, IS_SOURCE_KEY, IS_RIVER_END_KEY, FACE_BIOME_KEY)
        );
    }

    @Override
    public String getDescription() {
        return "Creates lake of given size where river cannot flow to the ocean until the lake are to big or the rivers" +
                "reach the ocean";
    }

    private TerrainMap terrainMap;
    private Random random;
    private RiverGenerator riverGenerator;
    private Set<Coord> newRiverStarts;
    private Set<Coord> newLakeStarts;

    private double maxLakeSize;

    @Override
    public void execute(TerrainMap map, Context context) {
        this.terrainMap = map;
        this.random = new Random(map.getProperty(SEED));
        this.riverGenerator = new RiverGenerator(terrainMap, HEIGHT_KEY);
        this.maxLakeSize = context.getParamOrDefault(LAKE_SIZE_PARAM);
        this.newRiverStarts = new HashSet<>();
        this.newLakeStarts = new HashSet<>();

        Coord lakeStart = getRiverEndInHole();
        while (lakeStart != null) {
            generateLake(lakeStart);
            lakeStart = getRiverEndInHole();
        }
        terrainMap.putProperty(HAS_LAKES_KEY, new MarkerType());
        newRiverStarts.forEach(start -> start.putProperty(VERTEX_WATER_KEY, new BooleanType(true)));
        removeRiversInLakeEdges(map.getProperty(FACES).stream().filter(
                face -> face.getProperty(WATER_KIND_KEY) == WaterKind.LAKE
        ).collect(Collectors.toSet()));
    }

    private void generateLake(Coord start) {
        if (newLakeStarts.contains(start)) {
            return;
        }
        newLakeStarts.add(start);
        Face baseLake = findLowestFace(start);
        double lakeHeight = baseLake.getCenter().getProperty(HEIGHT_KEY).value;
        Set<Face> lakeTiles = new HashSet<>();
        lakeTiles.add(baseLake);
        if (!turnIntoLake(baseLake, lakeHeight)) {
            return;
        }
        List<Coord> candidates = getRiverStartCandidates(lakeTiles);
        while (candidates.isEmpty() && lakeTiles.size() < maxLakeSize) {
            Face newLakeFace = findLowestNeighbour(lakeTiles);
            lakeTiles.add(newLakeFace);
            lakeHeight = getMaxHeight(lakeTiles);
            levelFaces(lakeTiles, lakeHeight);
            if (newLakeFace.getProperty(FACE_WATER_KEY).value) {
                if (newLakeFace.getProperty(WATER_KIND_KEY) == WaterKind.OCEAN) {
                    turnLakeIntoOcean(lakeTiles);
                }
                return;
            }
            if (!turnIntoLake(newLakeFace, lakeHeight)) {
                turnLakeIntoOcean(lakeTiles);
                return;
            }
            candidates = getRiverStartCandidates(lakeTiles);
        }
        if (!candidates.isEmpty()) {
            Set<Coord> seen = new HashSet<>();
            lakeTiles.forEach(tile -> seen.addAll(tile.getBorderVertices()));
            Coord riverStart = candidates.get(random.nextInt(candidates.size()));
            newRiverStarts.add(riverStart);
            riverStart.putProperty(VERTEX_WATER_KEY, new BooleanType(false));
            riverGenerator.generateRiverFrom(riverStart, seen);
        }
    }

    private Coord getRiverEndInHole() {
        Set<Coord> edgeVertices = new HashSet<>(terrainMap.getProperty(VERTICES));
        terrainMap.getProperty(FACES).forEach(face -> edgeVertices.remove(face.getCenter()));
        for (Coord vertex : edgeVertices) {
            if (vertex.hasProperty(IS_RIVER_END_KEY) && !vertex.getProperty(VERTEX_WATER_KEY).value && !newLakeStarts.contains(vertex)) {
                return vertex;
            }
        }
        return null;
    }

    private boolean turnIntoLake(Face face, double lakeHeight) {
        WaterKind kind = WaterKind.LAKE;
        for (Face connected : face.getNeighbors()) {
            if (connected.getProperty(WATER_KIND_KEY) == WaterKind.OCEAN) {
                kind = WaterKind.OCEAN;
                break;
            }
        }
        if (kind == WaterKind.OCEAN) {
            lakeHeight = 0;
        }
        for (Coord vertex : face.getBorderVertices()) {
            turnIntoWaterPoint(vertex, lakeHeight);
        }
        turnIntoWaterPoint(face.getCenter(), lakeHeight);
        face.putProperty(FACE_WATER_KEY, new BooleanType(true));
        face.putProperty(WATER_KIND_KEY, kind);
        face.putProperty(FACE_BIOME_KEY, Biome.LAKE);
        recalculateCenterHeight(face.getNeighbors());
        return kind == WaterKind.LAKE;
    }

    private void turnLakeIntoOcean(Set<Face> lake) {
        for (Face face : lake) {
            for (Coord vertex : face.getBorderVertices()) {
                turnIntoWaterPoint(vertex, 0);
            }
            turnIntoWaterPoint(face.getCenter(), 0);
            face.putProperty(WATER_KIND_KEY, WaterKind.OCEAN);
            face.putProperty(FACE_BIOME_KEY, Biome.OCEAN);
            recalculateCenterHeight(face.getNeighbors());
        }
    }

    private void turnIntoWaterPoint(Coord vertex, double height) {
        vertex.putProperty(VERTEX_WATER_KEY, new BooleanType(true));
        vertex.putProperty(HEIGHT_KEY, new DoubleType(height));
    }

    private List<Coord> getRiverStartCandidates(Set<Face> lake) {
        Set<Coord> result = new HashSet<>();
        for (Face face : lake) {
            for (Coord vertex : face.getBorderVertices()) {
                Set<Coord> connected = terrainMap.getProperty(EDGES).getConnectedVertices(vertex);
                for (Coord neighbour : connected) {
                    if (neighbour.getProperty(HEIGHT_KEY).value < vertex.getProperty(HEIGHT_KEY).value) {
                        result.add(vertex);
                        break;
                    }
                }
            }
        }
        List<Coord> list = new ArrayList<>(result);
        list.sort((o1, o2) -> (int) (o1.x + o1.y - o2.x - o2.y));
        return list;
    }

    private void levelFaces(Set<Face> faces, double level) {
        for (Face face : faces) {
            for (Coord vertex : face.getBorderVertices()) {
                vertex.putProperty(HEIGHT_KEY, new DoubleType(level));
            }
            face.putProperty(HEIGHT_KEY, new DoubleType(level));
        }
    }

    private Face findLowestFace(Coord start) {
        List<Face> candidates = new ArrayList<>();
        for (Face face : terrainMap.getProperty(FACES)) {
            Set<Coord> borders = face.getBorderVertices();
            if (borders.contains(start)) {
                candidates.add(face);
            }
        }
        Face min = candidates.get(0);
        for (int i = 1; i < candidates.size(); i++) {
            Face face = candidates.get(i);
            if (face.getCenter().getProperty(HEIGHT_KEY).value < min.getCenter().getProperty(HEIGHT_KEY).value) {
                min = face;
            }
        }
        return min;
    }

    private Face findLowestNeighbour(Set<Face> faces) {
        Face min = null;
        for (Face face : faces) {
            for (Face current : face.getNeighbors()) {
                if (faces.contains(current)) {
                    continue;
                }
                if (min == null || current.getCenter().getProperty(HEIGHT_KEY).value <
                        min.getCenter().getProperty(HEIGHT_KEY).value) {
                    min = current;
                }
            }
        }
        return min;
    }

    private double getMaxHeight(Set<Face> faces) {
        Face max = null;
        for (Face face : faces) {
            if (max == null || face.getCenter().getProperty(HEIGHT_KEY).value >
                    max.getCenter().getProperty(HEIGHT_KEY).value) {
                max = face;
            }
        }
        if (max == null) {
            throw new IllegalArgumentException("Set used for maxHeight is empty!");
        }
        return max.getCenter().getProperty(HEIGHT_KEY).value;
    }

    private void recalculateCenterHeight(Set<Face> faces) {
        for (Face face : faces) {
            double average = 0;
            for (Coord vertex : face.getBorderVertices()) {
                average += vertex.getProperty(HEIGHT_KEY).value;
            }
            face.getCenter().putProperty(HEIGHT_KEY, new DoubleType(average / face.getBorderVertices().size()));
        }
    }

    private void removeRiversInLakeEdges(Set<Face> lakes) {
        lakes.forEach(lake -> lake.getEdges().forEach(edge ->
                edge.putProperty(RIVER_FLOW_KEY, new OptionalIntegerType(0))));
    }

}
