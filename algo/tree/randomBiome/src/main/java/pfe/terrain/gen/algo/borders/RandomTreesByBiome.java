package pfe.terrain.gen.algo.borders;

import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.SerializableKey;
import pfe.terrain.gen.algo.island.Biome;
import pfe.terrain.gen.algo.island.IslandMap;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.island.geometry.Face;
import pfe.terrain.gen.algo.island.geometry.FaceSet;
import pfe.terrain.gen.algo.types.DoubleType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomTreesByBiome extends Contract {


    static final Key<Biome> FACE_BIOME_KEY =
            new SerializableKey<>(FACES_PREFIX + "BIOME", "biome", Biome.class);

    static final SerializableKey<DoubleType> FACE_PITCH_KEY =
            new SerializableKey<>(FACES_PREFIX + "HAS_PITCH", "pitch", DoubleType.class);

    static final SerializableKey<TreeType> TREES_KEY =
            new SerializableKey<>("TREES", "trees", TreeType.class);

    static final Key<DoubleType> VERTEX_HEIGHT_KEY =
            new SerializableKey<>(VERTICES_PREFIX + "HEIGHT", "height", DoubleType.class);


    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(FACES, FACE_BIOME_KEY, FACE_PITCH_KEY, SEED, VERTEX_HEIGHT_KEY),
                asKeySet(TREES_KEY)
        );
    }

    @Override
    public void execute(IslandMap islandMap, Context context) {
        FaceSet faces = islandMap.getFaces();
        List<JsonCoord> coords = new ArrayList<>();
        Random random = new Random(islandMap.getSeed());
        Biome faceBiome;
        double pitch, z1, z2, z3, l1, l2, det;
        for (Face face : faces) {
            faceBiome = face.getProperty(FACE_BIOME_KEY);
            // pitch influence, high pitch means less trees
            pitch = 1 - (face.getProperty(FACE_PITCH_KEY).value / 300);
            if (pitch < 0) {
                pitch = 0;
            }
            if (faceBiome != Biome.OCEAN && faceBiome != Biome.LAKE && faceBiome != Biome.GLACIER) {
                for (Coord[] triangle : face.getTriangles()) {
                    z1 = triangle[0].getProperty(VERTEX_HEIGHT_KEY).value;
                    z2 = triangle[1].getProperty(VERTEX_HEIGHT_KEY).value;
                    z3 = triangle[2].getProperty(VERTEX_HEIGHT_KEY).value;
                    det = (triangle[1].y - triangle[2].y) * (triangle[0].x - triangle[2].x) + (triangle[2].x - triangle[1].x) * (triangle[0].y - triangle[2].y);
                    for (int i = 0; i < Math.round(random.nextInt(6) * faceBiome.getTreeDensity() * pitch); i++) {
                        // Get x and y from a random point inside a triangle
                        Coord c = Face.getRandomPointInsideTriangle(triangle, random);
                        // Linear interpolation from barycentric coordinates to get z
                        l1 = ((triangle[1].y - triangle[2].y) * (c.x - triangle[2].x) + (triangle[2].x - triangle[1].x) * (c.y - triangle[2].y)) / det;
                        l2 = ((triangle[2].y - triangle[0].y) * (c.x - triangle[2].x) + (triangle[0].x - triangle[2].x) * (c.y - triangle[2].y)) / det;
                        coords.add(new JsonCoord(c.x, c.y, l1 * z1 + l2 * z2 + (1 - l1 - l2) * z3));
                    }
                }
            }
        }
        islandMap.putProperty(TREES_KEY, new TreeType(coords));
    }
}
