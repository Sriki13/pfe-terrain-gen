package pfe.terrain.gen.water;

import com.flowpowered.noise.module.Module;
import com.flowpowered.noise.module.source.Billow;
import com.flowpowered.noise.module.source.Perlin;
import com.flowpowered.noise.module.source.RidgedMulti;
import pfe.terrain.gen.algo.*;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.geometry.Coord;
import pfe.terrain.gen.algo.geometry.Face;
import pfe.terrain.gen.algo.types.BooleanType;

import java.util.Arrays;
import java.util.Set;
import java.util.logging.Logger;

public class NoiseWaterGeneration extends Contract {

    static final Param<String> noiseParam = new Param<>("NoiseType", String.class,
            Arrays.toString(Noise.values()),
            "Choose the noise algorithm to use : Perlin : classic island, Billow : small and round islands, Ridged : aggressive geology with big island in the middle and a lot of reef",
            Noise.PERLIN.getNoiseName());
    static final Param<Double> archipelagoTendencyParam = new Param<>("archipelagoTendency", Double.class,
            "0-1", "Tendency of multiple islands to spawn, (0 = not a lot, 1.0 = max)", 0.0);
    static final Param<Double> coastRoughnessParam = new Param<>("coastRoughness", Double.class,
            "0-1", "Makes the border of the islands appear more smooth (0.0) or tough (1.0)", 0.3);


    static final Key<BooleanType> faceWaterKey = new SerializableKey<>(facesPrefix + "IS_WATER", "isWater", BooleanType.class);
    private static final Key<BooleanType> vertexWaterKey = new SerializableKey<>(verticesPrefix + "IS_WATER", "isWater", BooleanType.class);
    static final Key<WaterKind> waterKindKey = new SerializableKey<>(facesPrefix + "WATER_KIND", "waterKind", WaterKind.class);

    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(faces, vertices, seed),
                asKeySet(faceWaterKey, vertexWaterKey, waterKindKey)
        );
    }

    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(archipelagoTendencyParam, coastRoughnessParam, noiseParam);
    }

    @Override
    public void execute(IslandMap map, Context context) {
        double archipelagoTendency = context.getParamOrDefault(archipelagoTendencyParam);
        double coastRoughness = context.getParamOrDefault(coastRoughnessParam);
        Noise algorithm;
        try {
            algorithm = Noise.valueOf(context.getParamOrDefault(noiseParam).toUpperCase());
        } catch (IllegalArgumentException e) {
            Logger.getLogger(this.getName()).warning("No Style marching argument " + context.getParamOrDefault(noiseParam)
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
            face.putProperty(faceWaterKey, isWater);
            if (isWater.value) {
                face.putProperty(waterKindKey, WaterKind.OCEAN);
            } else {
                face.putProperty(waterKindKey, WaterKind.NONE);
            }
            face.getCenter().putProperty(vertexWaterKey, isWater);
            for (Coord coord : face.getBorderVertices()) {
                coord.putProperty(vertexWaterKey, isWater);
            }
        }
    }

    private boolean isWater(Module perlin, double x, double y, double smoothingFactor) {
        double c = (perlin.getValue(x, y, 0));
        double length = Math.abs(Math.pow(x, 2) + Math.pow(y, 2)) * (1 + smoothingFactor);
        return c < length;
    }
}
