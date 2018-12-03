package pfe.terrain.gen;

import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.context.Context;
import pfe.terrain.gen.algo.geometry.Face;
import pfe.terrain.gen.algo.island.IslandMap;
import pfe.terrain.gen.algo.island.WaterKind;
import pfe.terrain.gen.algo.key.Key;
import pfe.terrain.gen.algo.key.Param;
import pfe.terrain.gen.algo.key.SerializableKey;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.criteria.*;

import java.util.*;

import static pfe.terrain.gen.criteria.HeightLevel.HEIGHT_KEY;
import static pfe.terrain.gen.criteria.LakeProximity.LAKES_KEY;
import static pfe.terrain.gen.criteria.MoistureLevel.MOISTURE_KEY;
import static pfe.terrain.gen.criteria.Pitch.PITCH_KEY;
import static pfe.terrain.gen.criteria.RiverProximity.RIVER_FLOW_KEY;

public class CityGenerator extends Contract {

    public static final Param<Integer> NB_CITIES = new Param<>("nbCities", Integer.class, 0, 10,
            "The number of cities added to the island", 3, "Number of cities");

    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(NB_CITIES);
    }

    // Required

    public static final Key<WaterKind> WATER_KIND_KEY =
            new Key<>(facesPrefix + "WATER_KIND", WaterKind.class);


    // Produced

    public static final Key<BooleanType> CITY_KEY =
            new SerializableKey<>(facesPrefix + "HAS_CITY", "isCity", BooleanType.class);


    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(PITCH_KEY, RIVER_FLOW_KEY, HEIGHT_KEY, MOISTURE_KEY,
                        LAKES_KEY, WATER_KIND_KEY, faces, edges, vertices),
                asKeySet(CITY_KEY)
        );
    }

    @Override
    public void execute(IslandMap map, Context context) {
        map.getFaces().forEach(face -> face.putProperty(CITY_KEY, new BooleanType(false)));
        Map<Face, Double> scores = new HashMap<>();
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
        land.forEach(face -> scores.put(face, 0.0));
        List<Criterion> criteria = Arrays.asList(
                new HeightLevel(land),
                new LakeProximity(lakes),
                new MoistureLevel(),
                new Pitch(),
                new RiverProximity(map.getEdges())
        );
        criteria.forEach(criterion -> criterion.assignScores(scores));
        CityProximity cityProximity = new CityProximity();
        for (int i = 0; i < context.getParamOrDefault(NB_CITIES); i++) {
            Map<Face, Double> scoresCopy = new HashMap<>(scores);
            cityProximity.assignScores(scoresCopy);
            Face newCity = Collections.max(scoresCopy.entrySet(),
                    (a, b) -> (int) (a.getValue() - b.getValue())).getKey();
            newCity.putProperty(CITY_KEY, new BooleanType(true));
            cityProximity.addCity(newCity);
            scores.remove(newCity);
        }
    }

}
