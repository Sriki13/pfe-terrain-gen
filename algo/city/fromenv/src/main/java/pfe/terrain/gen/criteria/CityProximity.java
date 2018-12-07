package pfe.terrain.gen.criteria;

import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.island.geometry.Face;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CityProximity implements Criterion {

    public static final Param<Double> CITY_PROXIMITY_WEIGHT =
            Param.generateDefaultDoubleParam("cityProximityWeight",
                    "How close cities are allowed to be. Lower values mean cities will spawn closer to each other", 1,
                    "City proximity");

    private Set<Face> cityFaces = new HashSet<>();

    public void addCity(Face face) {
        cityFaces.add(face);
    }

    @Override
    public void assignScores(Context context, Map<Face, Double> scores) {
        double weight = context.getParamOrDefault(CITY_PROXIMITY_WEIGHT) * 1000;
        cityFaces.forEach(cityFace ->
                scores.forEach((key, value) ->
                        scores.put(key, value - (1 / (cityFace.getCenter().distance(key.getCenter()) + 0.1)) * weight)));
    }

}
