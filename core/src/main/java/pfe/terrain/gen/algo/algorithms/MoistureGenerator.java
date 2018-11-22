package pfe.terrain.gen.algo.algorithms;

import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.SerializableKey;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;

import java.util.Set;

public abstract class MoistureGenerator extends Contract {

    protected final SerializableKey<DoubleType> faceMoisture = new SerializableKey<>(facesPrefix + "HAS_MOISTURE", "moisture", DoubleType.class);
    protected final Key<Double> minMoisture = new Key<>(facesPrefix + "MIN_MOISTURE", Double.class);
    protected final Key<Double> maxMoisture = new Key<>(facesPrefix + "MAX_MOISTURE", Double.class);

    protected final Key<BooleanType> faceWaterKey = new SerializableKey<>(facesPrefix + "IS_WATER", "isWater", BooleanType.class);


    @Override
    public Set<Key> getRequestedParameters() {
        return asSet(minMoisture, maxMoisture, faceWaterKey);
    }

    @Override
    public Constraints getContract() {
        return new Constraints(asSet(faces, seed), asSet(faceMoisture));
    }
}
