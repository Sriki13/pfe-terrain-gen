package pfe.terrain.gen.algo.borders;

import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.constraints.key.SerializableKey;
import pfe.terrain.gen.algo.island.Biome;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.island.geometry.Face;
import pfe.terrain.gen.algo.island.geometry.FaceSet;
import pfe.terrain.gen.algo.types.DoubleType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class RandomTreesByBiome extends Contract {

    private static final Param<Double> PITCH_IMPORTANCE = Param.generateDefaultDoubleParam("pitchImportance",
            "How the pitch affects the tree spawn chance : 0=no effect, 1=huge effect", 0.5, "Importance of face pitch");

    private static final Param<Double> TREE_DENSITY = Param.generateDefaultDoubleParam("treeDensity",
            "How many trees are generated per face : 0=very few, 1=a lot", 0.5, "Density/Number of trees");

    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(PITCH_IMPORTANCE, TREE_DENSITY);
    }

    private static final Key<Biome> FACE_BIOME_KEY =
            new SerializableKey<>(FACES_PREFIX + "BIOME", "biome", Biome.class);

    private static final SerializableKey<DoubleType> FACE_PITCH_KEY =
            new SerializableKey<>(FACES_PREFIX + "PITCH", "pitch", DoubleType.class);

    private static final SerializableKey<TreeType> TREES_KEY =
            new SerializableKey<>("TREES", "trees", TreeType.class);

    private static final Key<DoubleType> VERTEX_HEIGHT_KEY =
            new SerializableKey<>(VERTICES_PREFIX + "HEIGHT", "height", DoubleType.class);


    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(FACES, FACE_BIOME_KEY, FACE_PITCH_KEY, SEED, VERTEX_HEIGHT_KEY),
                asKeySet(TREES_KEY)
        );
    }

    @Override
    public void execute(TerrainMap terrainMap, Context context) {
        FaceSet faces = terrainMap.getProperty(FACES);
        double treeDensity = 3 + 3 * context.getParamOrDefault(TREE_DENSITY);
        double pitchImportance = 500 - (320 * context.getParamOrDefault(PITCH_IMPORTANCE));
        List<Coord3D> coords = new ArrayList<>();
        Random random = new Random(terrainMap.getProperty(SEED));
        Biome faceBiome;
        double pitch, z1, z2, z3, l1, l2, det;
        for (Face face : faces) {
            faceBiome = face.getProperty(FACE_BIOME_KEY);
            // pitch influence, high pitch means less trees
            pitch = 1 - (face.getProperty(FACE_PITCH_KEY).value / pitchImportance);
            if (pitch < 0) {
                pitch = 0;
            }
            // Some biome like OCEAN or LAKE are ignored
            if (faceBiome != Biome.OCEAN && faceBiome != Biome.LAKE && faceBiome != Biome.GLACIER) {
                for (Coord[] triangle : face.getTriangles()) {
                    z1 = triangle[0].getProperty(VERTEX_HEIGHT_KEY).value;
                    z2 = triangle[1].getProperty(VERTEX_HEIGHT_KEY).value;
                    z3 = triangle[2].getProperty(VERTEX_HEIGHT_KEY).value;
                    det = (triangle[1].y - triangle[2].y) * (triangle[0].x - triangle[2].x) + (triangle[2].x - triangle[1].x) * (triangle[0].y - triangle[2].y);
                    for (int i = 0; i < Math.round(random.nextInt((int) treeDensity) * faceBiome.getTreeDensity() * pitch); i++) {
                        // Get x and y from a random point inside a triangle
                        Coord c = Face.getRandomPointInsideTriangle(triangle, random);
                        // Linear interpolation from barycentric coordinates to get z
                        l1 = ((triangle[1].y - triangle[2].y) * (c.x - triangle[2].x) + (triangle[2].x - triangle[1].x) * (c.y - triangle[2].y)) / det;
                        l2 = ((triangle[2].y - triangle[0].y) * (c.x - triangle[2].x) + (triangle[0].x - triangle[2].x) * (c.y - triangle[2].y)) / det;
                        coords.add(new Coord3D(c.x, c.y, l1 * z1 + l2 * z2 + (1 - l1 - l2) * z3));
                    }
                }
            }
        }
        terrainMap.putProperty(TREES_KEY, new TreeType(coords));
    }
}
