package pfe.terrain.gen.cave;

import com.flowpowered.noise.module.Module;
import com.flowpowered.noise.module.source.Perlin;
import com.flowpowered.noise.module.source.RidgedMulti;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.SerializableKey;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.geometry.Face;
import pfe.terrain.gen.algo.types.BooleanType;

import java.util.HashSet;
import java.util.Set;

public class NoiseWall extends Contract {

    static final Key<BooleanType> FACE_WALL_KEY =
            new SerializableKey<>(FACES_PREFIX + "IS_WALL", "isWall", BooleanType.class);

    static final Key<BooleanType> VERTEX_WALL_KEY =
            new SerializableKey<>(VERTICES_PREFIX + "IS_WALL", "isWall", BooleanType.class);

    @Override
    public Constraints getContract() {
        return new Constraints(asKeySet(FACES, SEED, FACE_WALL_KEY), asKeySet());
    }

    @Override
    public String getDescription() {
        return "Adds all around a noise generated function";
    }

    @Override
    public void execute(TerrainMap map, Context context) {
        Set<Face> wallFaces = new HashSet<>();
        Set<Face> emptyFaces = new HashSet<>();
        int size = map.getProperty(SIZE);
        double borderSmoothingFactor = 0.3;
        Module noise;
        Noise n = Noise.PERLIN;
        switch (n) {
            case PERLIN:
                noise = new Perlin();
                ((Perlin) noise).setSeed(map.getProperty(SEED));
                ((Perlin) noise).setFrequency(3);
                ((Perlin) noise).setLacunarity(1.2);
                ((Perlin) noise).setPersistence(0.9);
                ((Perlin) noise).setOctaveCount(10);
                borderSmoothingFactor = 0.5;
                break;
            case RIDGED:
            default:
                noise = new RidgedMulti();
                ((RidgedMulti) noise).setSeed(map.getProperty(SEED));
                ((RidgedMulti) noise).setFrequency(2);
                ((RidgedMulti) noise).setLacunarity(2);
                break;
        }
        for (Face face : map.getProperty(FACES)) {
            if (isWall(noise, 2 * (face.getCenter().x / size - 0.5), 2 * (face.getCenter().y / size - 0.5), borderSmoothingFactor)) {
                wallFaces.add(face);
            } else {
                emptyFaces.add(face);
            }
        }
        for (Face face : emptyFaces) {
            putWallKeys(face, new BooleanType(false));
        }
        for (Face face : wallFaces) {
            putWallKeys(face, new BooleanType(true));
        }
    }

    private void putWallKeys(Face face, BooleanType b) {
        face.getCenter().putProperty(VERTEX_WALL_KEY, b);
        face.getBorderVertices().forEach(v -> v.putProperty(VERTEX_WALL_KEY, b));
        face.putProperty(FACE_WALL_KEY, b);
    }

    private boolean isWall(Module perlin, double x, double y, double smoothingFactor) {
        double c = (perlin.getValue(x, y, 0));
        double smoothing = 2 * Math.max(Math.abs(x), Math.abs(y));
        //smoothing += (Math.abs(x) + Math.abs(y)) / 3;
        return c < smoothing;
    }
}
