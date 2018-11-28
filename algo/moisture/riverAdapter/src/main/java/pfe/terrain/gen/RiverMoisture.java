package pfe.terrain.gen;

import pfe.terrain.gen.algo.*;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.geometry.Edge;
import pfe.terrain.gen.algo.geometry.Face;
import pfe.terrain.gen.algo.types.DoubleType;
import pfe.terrain.gen.algo.types.IntegerType;

import java.util.HashSet;
import java.util.Set;

public class RiverMoisture extends Contract {

    public static final Key<DoubleType> faceMoisture =
            new SerializableKey<>(facesPrefix + "HAS_MOISTURE", "moisture", DoubleType.class);

    public static final Key<IntegerType> riverFlowKey =
            new Key<>(edgesPrefix + "RIVER_FLOW", IntegerType.class);

    @Override
    public Constraints getContract() {
        return new Constraints(asKeySet(faces, edges, riverFlowKey), asKeySet(), asKeySet(faceMoisture));
    }

    private final Param<Double> riverMoistureParam = new Param<>("riverMoisture", Double.class, "0-1",
            "The amount of moisture added around the rivers.", 0.5);

    public Set<Param> getRequestedParameters() {
        return asParamSet(riverMoistureParam);
    }

    private static final double MAX_ADD = 0.5;
    private static final double MIN_ADD = 0.1;

    @Override
    public void execute(IslandMap map, Context context) throws DuplicateKeyException, KeyTypeMismatch, NoSuchKeyException {
        double moistureBonus = (MAX_ADD - MIN_ADD) * (context.getParamOrDefault(riverMoistureParam)) + MIN_ADD;
        Set<Face> nextToRiver = new HashSet<>();
        for (Face face : map.getFaces()) {
            for (Edge edge : face.getEdges()) {
                if (edge.getProperty(riverFlowKey).value > 0) {
                    nextToRiver.add(face);
                    break;
                }
            }
        }
        Set<Face> seen = new HashSet<>(nextToRiver);
        for (Face face : nextToRiver) {
            addMoisture(face, moistureBonus);
            spreadToNeighbours(face, seen, moistureBonus / 2);
        }
    }

    private void addMoisture(Face face, double bonus)
            throws NoSuchKeyException, KeyTypeMismatch, DuplicateKeyException {
        double newVal = face.getProperty(faceMoisture).value + bonus;
        if (newVal > 1) newVal = 1;
        face.putProperty(faceMoisture, new DoubleType(newVal));
    }

    private void spreadToNeighbours(Face start, Set<Face> seen, double bonus)
            throws DuplicateKeyException, NoSuchKeyException, KeyTypeMismatch {
        for (Face neighbor : start.getNeighbors()) {
            if (!seen.contains(neighbor)) {
                addMoisture(neighbor, bonus);
                seen.add(neighbor);
            }
        }
    }

}
