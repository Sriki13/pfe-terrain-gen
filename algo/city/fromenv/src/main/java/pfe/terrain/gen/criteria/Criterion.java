package pfe.terrain.gen.criteria;

import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.island.geometry.Face;

import java.util.Map;

public interface Criterion {

    void assignScores(Context context, Map<Face, Double> scores);

}
