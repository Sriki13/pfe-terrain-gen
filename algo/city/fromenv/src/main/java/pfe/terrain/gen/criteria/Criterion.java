package pfe.terrain.gen.criteria;

import pfe.terrain.gen.algo.geometry.Face;

import java.util.Map;

public interface Criterion {

    void assignScores(Map<Face, Double> scores);

}
