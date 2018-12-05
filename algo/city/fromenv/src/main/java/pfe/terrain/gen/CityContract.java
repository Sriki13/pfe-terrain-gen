package pfe.terrain.gen;

import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.OptionalKey;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.constraints.key.SerializableKey;
import pfe.terrain.gen.algo.island.IslandMap;
import pfe.terrain.gen.algo.island.WaterKind;
import pfe.terrain.gen.algo.island.geometry.Face;
import pfe.terrain.gen.algo.types.MarkerType;
import pfe.terrain.gen.criteria.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static pfe.terrain.gen.criteria.HeightLevel.HEIGHT_KEY;
import static pfe.terrain.gen.criteria.MoistureLevel.MOISTURE_KEY;
import static pfe.terrain.gen.criteria.Pitch.PITCH_KEY;
import static pfe.terrain.gen.criteria.RiverProximity.RIVER_FLOW_KEY;

public class CityContract extends Contract {

    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(
                CityGenerator.NB_CITIES,
                CityProximity.CITY_PROXIMITY_WEIGHT,
                HeightLevel.CITY_HEIGHT_WEIGHT, HeightLevel.CITY_MIN_HEIGHT, HeightLevel.CITY_MAX_HEIGHT,
                LakeProximity.CITY_LAKE_WEIGHT,
                MoistureLevel.CITY_MOISTURE_WEIGHT,
                Pitch.CITY_PITCH_WEIGHT,
                RiverProximity.CITY_RIVER_WEIGHT
        );
    }

    // Required

    public static final Key<WaterKind> WATER_KIND_KEY =
            new Key<>(FACES_PREFIX + "WATER_KIND", WaterKind.class);


    // Produced

    public static final Key<MarkerType> CITY_KEY =
            new SerializableKey<>(new OptionalKey<>(FACES_PREFIX + "HAS_CITY", MarkerType.class), "isCity");


    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(PITCH_KEY, RIVER_FLOW_KEY, HEIGHT_KEY, MOISTURE_KEY,
                        WATER_KIND_KEY, FACES, EDGES, VERTICES),
                asKeySet(CITY_KEY)
        );
    }

    @Override
    public void execute(IslandMap map, Context context) {
        Set<Face> land = new HashSet<>();
        Set<Face> lakes = new HashSet<>();
        map.getFaces().forEach(face -> {
            WaterKind kind = face.getProperty(WATER_KIND_KEY);
            if (kind == WaterKind.NONE) {
                land.add(face);
            } else if (kind == WaterKind.LAKE) {
                lakes.add(face);
            }
        });
        CityGenerator generator = new CityGenerator(Arrays.asList(
                new HeightLevel(land),
                new LakeProximity(lakes),
                new MoistureLevel(),
                new Pitch(),
                new RiverProximity(map.getEdges())
        ));
        generator.generateCities(context, land);
    }
}
