package pfe.terrain.gen.water;

import com.flowpowered.noise.module.Module;
import com.flowpowered.noise.module.source.Billow;
import com.flowpowered.noise.module.source.Perlin;
import com.flowpowered.noise.module.source.RidgedMulti;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.constraints.key.SerializableKey;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.island.IslandMap;
import pfe.terrain.gen.algo.island.WaterKind;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.island.geometry.Face;
import pfe.terrain.gen.algo.types.BooleanType;

import java.util.Arrays;
import java.util.Set;
import java.util.logging.Logger;

public class NoiseWaterGeneration extends Contract {

    static final Param<String> NOISE_PARAM = new Param<>("NoiseType", String.class,
            Arrays.toString(Noise.values()),
            "Choose the noise algorithm to use : Perlin : classic island, Billow : small and round islands, " +
                    "Ridged : aggressive geology with big island in the middle and a lot of reef",
            Noise.PERLIN.getNoiseName(), "Noise type");

    static final Param<Double> ARCHIPELAGO_TENDENCY_PARAM = Param.generateDefaultDoubleParam("archipelagoTendency",
            "Tendency of multiple islands to spawn, (0 = not a lot, 1.0 = max)", 0.0, "Number of islands");
    static final Param<Double> COAST_ROUGHNESS_PARAM = Param.generateDefaultDoubleParam("coastRoughness",
            "Makes the border of the islands appear more smooth (0.0) or rough (1.0)", 0.3, "Island border roughness");

    static final Key<BooleanType> FACE_WATER_KEY =
            new SerializableKey<>(FACES_PREFIX + "IS_WATER", "isWater", BooleanType.class);

    private static final Key<BooleanType> VERTEX_WATER_KEY =
            new SerializableKey<>(VERTICES_PREFIX + "IS_WATER", "isWater", BooleanType.class);

    static final Key<WaterKind> WATER_KIND_KEY =
            new SerializableKey<>(FACES_PREFIX + "WATER_KIND", "waterKind", WaterKind.class);

    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(FACES, VERTICES, SEED),
                asKeySet(FACE_WATER_KEY, VERTEX_WATER_KEY, WATER_KIND_KEY)
        );
    }

    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(ARCHIPELAGO_TENDENCY_PARAM, COAST_ROUGHNESS_PARAM, NOISE_PARAM);
    }

    @Override
    public void execute(IslandMap map, Context context) {
        double archipelagoTendency = context.getParamOrDefault(ARCHIPELAGO_TENDENCY_PARAM);
        double coastRoughness = context.getParamOrDefault(COAST_ROUGHNESS_PARAM);
        Noise algorithm;
        try {
            algorithm = Noise.valueOf(context.getParamOrDefault(NOISE_PARAM).toUpperCase());
        } catch (IllegalArgumentException e) {
            Logger.getLogger(this.getName()).warning("No Style marching argument " + context.getParamOrDefault(NOISE_PARAM)
                    + ", defaulting to classic style");
            algorithm = Noise.PERLIN;
        }
        int size = map.getSize();
        double borderSmoothingFactor;
        Module noise;
        switch (algorithm) {
            case BILLOW:
                borderSmoothingFactor = 0.15;
                noise = new Billow();
                ((Billow) noise).setSeed(map.getSeed());
                ((Billow) noise).setFrequency(1.0 + archipelagoTendency * 2);
                ((Billow) noise).setLacunarity(1.5 + coastRoughness);
                break;
            case RIDGED:
                borderSmoothingFactor = 0.8;
                noise = new RidgedMulti();
                ((RidgedMulti) noise).setSeed(map.getSeed());
                ((RidgedMulti) noise).setFrequency(0.5 + archipelagoTendency * 2.5);
                ((RidgedMulti) noise).setLacunarity(1.0 + coastRoughness);
                break;
            case PERLIN:
            default:
                borderSmoothingFactor = 0.3;
                noise = new Perlin();
                ((Perlin) noise).setSeed(map.getSeed());
                ((Perlin) noise).setFrequency(0.75 + archipelagoTendency * 3);
                ((Perlin) noise).setLacunarity(1.5 + coastRoughness);
                break;
        }
        for (Face face : map.getFaces()) {
            BooleanType isWater = new BooleanType(isWater(noise, 2 * (face.getCenter().x / size - 0.5), 2 * (face.getCenter().y / size - 0.5), borderSmoothingFactor));
            face.putProperty(FACE_WATER_KEY, isWater);
            if (isWater.value) {
                face.putProperty(WATER_KIND_KEY, WaterKind.OCEAN);
            } else {
                face.putProperty(WATER_KIND_KEY, WaterKind.NONE);
            }
            face.getCenter().putProperty(VERTEX_WATER_KEY, isWater);
            //noinspection Duplicates
            for (Coord coord : face.getBorderVertices()) {
                try {
                    coord.getProperty(VERTEX_WATER_KEY);
                    if (isWater.value) {
                        coord.putProperty(VERTEX_WATER_KEY, isWater);
                    }
                } catch (NoSuchKeyException ke) {
                    coord.putProperty(VERTEX_WATER_KEY, isWater);
                }
            }
        }
    }

    private boolean isWater(Module perlin, double x, double y, double smoothingFactor) {
        double c = (perlin.getValue(x, y, 0));
        double length = Math.abs(Math.pow(x, 2) + Math.pow(y, 2)) * (1 + smoothingFactor);
        return c < length;
    }
}
