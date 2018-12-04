package pfe.terrain.gen;

import pfe.terrain.gen.algo.island.geometry.Face;
import pfe.terrain.gen.algo.types.MarkerType;
import pfe.terrain.gen.criteria.CityProximity;
import pfe.terrain.gen.criteria.Criterion;

import java.util.*;

import static pfe.terrain.gen.CityContract.CITY_KEY;

public class CityGenerator {

    private List<Criterion> criteria;

    public CityGenerator(List<Criterion> criteria) {
        this.criteria = criteria;
    }

    public void generateCities(int nbCity, Set<Face> candidates) {
        Map<Face, Double> scores = new HashMap<>();
        candidates.forEach(face -> scores.put(face, 0.0));
        criteria.forEach(criterion -> criterion.assignScores(scores));
        CityProximity cityProximity = new CityProximity();
        for (int i = 0; i < nbCity; i++) {
            Map<Face, Double> scoresCopy = new HashMap<>(scores);
            cityProximity.assignScores(scoresCopy);
            Face newCity = Collections.max(scoresCopy.entrySet(),
                    (a, b) -> (int) (a.getValue() - b.getValue())).getKey();
            newCity.putProperty(CITY_KEY, new MarkerType());
            cityProximity.addCity(newCity);
            scores.remove(newCity);
        }
    }

}
