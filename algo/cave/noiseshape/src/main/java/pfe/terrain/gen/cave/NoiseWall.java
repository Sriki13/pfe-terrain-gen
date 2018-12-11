package pfe.terrain.gen.cave;

import com.flowpowered.noise.module.Module;
import com.flowpowered.noise.module.source.Perlin;
import com.flowpowered.noise.module.source.RidgedMulti;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.constraints.key.SerializableKey;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.geometry.Face;
import pfe.terrain.gen.algo.types.BooleanType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class NoiseWall extends Contract {

    static final Param<String> NOISE_PARAM = new Param<>("noiseType", String.class,
            Arrays.toString(Noise.values()),
            "Choose the noise algorithm to use",
            Noise.RIDGED.getNoiseName(), "Noise type for cave shape");

    static final Param<Double> MULTIPLE_CAVES_TENDENCY = Param.generateDefaultDoubleParam("multipleCaveTendency",
            "Tendency of multiple caves to spawn, (0 = not a lot, 1.0 = max)", 0.5, "Number of caves");
    static final Param<Double> CAVE_ROUGHNESS = Param.generateDefaultDoubleParam("caveRoughness",
            "Makes the borders of the walls appear more smooth (0.0) or rough (1.0)", 0.5, "cave walls roughness");

    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(CAVE_ROUGHNESS, MULTIPLE_CAVES_TENDENCY, NOISE_PARAM);
    }

    static final Key<BooleanType> FACE_WALL_KEY =
            new SerializableKey<>(FACES_PREFIX + "IS_WALL", "isWall", BooleanType.class);

    static final Key<BooleanType> VERTEX_WALL_KEY =
            new SerializableKey<>(VERTICES_PREFIX + "IS_WALL", "isWall", BooleanType.class);

    @Override
    public Constraints getContract() {
        return new Constraints(asKeySet(FACES, SEED),
                asKeySet(FACE_WALL_KEY, VERTEX_WALL_KEY));
    }

    @Override
    public String getDescription() {
        return "Adds walls with a noise generating function";
    }

    @Override
    public void execute(TerrainMap map, Context context) {
        double caveRoughness = context.getParamOrDefault(CAVE_ROUGHNESS);
        double multipleCaveTendency = context.getParamOrDefault(MULTIPLE_CAVES_TENDENCY);
        Set<Face> wallFaces = new HashSet<>();
        Set<Face> emptyFaces = new HashSet<>();
        int size = map.getProperty(SIZE);
        Module noise;
        double borderSmoothing;
        Noise n = Noise.valueOf(context.getParamOrDefault(NOISE_PARAM).toUpperCase());
        switch (n) {
            case PERLIN:
                noise = new Perlin();
                ((Perlin) noise).setSeed(map.getProperty(SEED));
                ((Perlin) noise).setFrequency(1.7 + multipleCaveTendency / 5.0);
                ((Perlin) noise).setLacunarity(1 + caveRoughness / 5.0);
                ((Perlin) noise).setPersistence(1.05);
                ((Perlin) noise).setOctaveCount(8 + (int) caveRoughness * 3);
                borderSmoothing = 1.7;
                break;
            case RIDGED:
            default:
                noise = new RidgedMulti();
                ((RidgedMulti) noise).setSeed(map.getProperty(SEED));
                ((RidgedMulti) noise).setFrequency(1.3 + multipleCaveTendency*1.2);
                ((RidgedMulti) noise).setLacunarity(0.8 + caveRoughness*1.3);
                ((RidgedMulti) noise).setOctaveCount(7 + (int) caveRoughness * 2);
                borderSmoothing = 0.8;
                break;
        }
        for (Face face : map.getProperty(FACES)) {
            if (isWall(noise, 2 * (face.getCenter().x / size - 0.5), 2 * (face.getCenter().y / size - 0.5), borderSmoothing)) {
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

    private boolean isWall(Module perlin, double x, double y, double borderSmoothing) {
        double c = (perlin.getValue(x, y, 0));
        double maxi = Math.max(Math.abs(x), Math.abs(y));
        double smoothing = borderSmoothing * maxi;
        if (maxi > 0.8) {
            smoothing += (maxi - 0.8) * 12;
        }
        smoothing += (Math.abs(x) + Math.abs(y)) / 5;
        return c < smoothing;
    }
}
