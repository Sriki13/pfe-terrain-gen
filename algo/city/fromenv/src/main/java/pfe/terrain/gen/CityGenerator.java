package pfe.terrain.gen;

import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.island.geometry.Face;
import pfe.terrain.gen.algo.types.MarkerType;
import pfe.terrain.gen.criteria.CityProximity;
import pfe.terrain.gen.criteria.Criterion;

import java.util.*;

import static pfe.terrain.gen.CityContract.CITY_KEY;

public class CityGenerator {

    public static final Param<Integer> NB_CITIES = new Param<>("nbCities", Integer.class, 0, 50,
            "The number of cities added to the island", 3, "Number of cities");

    private List<Criterion> criteria;

    public CityGenerator(List<Criterion> criteria) {
        this.criteria = criteria;
    }

    public void generateCities(Context context, Set<Face> candidates) {
        Map<Face, Double> scores = new HashMap<>();
        candidates.forEach(face -> scores.put(face, 0.0));
        criteria.forEach(criterion -> criterion.assignScores(context, scores));
        CityProximity cityProximity = new CityProximity();
        int nbCity = context.getParamOrDefault(NB_CITIES);
        for (int i = 0; i < nbCity; i++) {
            Map<Face, Double> scoresCopy = new HashMap<>(scores);
            cityProximity.assignScores(context, scoresCopy);
            Face newCity = Collections.max(scoresCopy.entrySet(),
                    (a, b) -> (int) (a.getValue() - b.getValue())).getKey();
            newCity.putProperty(CITY_KEY, new MarkerType());
            cityProximity.addCity(newCity);
            scores.remove(newCity);
        }
    }

}
