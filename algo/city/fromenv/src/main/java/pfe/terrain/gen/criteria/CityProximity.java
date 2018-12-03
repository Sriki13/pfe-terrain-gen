package pfe.terrain.gen.criteria;

import pfe.terrain.gen.algo.geometry.Face;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CityProximity implements Criterion {

    private static final int WEIGHT = 1;

    private Set<Face> cityFaces = new HashSet<>();

    public void addCity(Face face) {
        cityFaces.add(face);
    }

    @Override
    public void assignScores(Map<Face, Double> scores) {
        cityFaces.forEach(cityFace ->
                scores.forEach((key, value) ->
                        scores.put(key, value - (1 / (cityFace.getCenter().distance(key.getCenter()) + 0.1)) * WEIGHT)));
    }

}
