package pfe.terrain.gen.algo.algorithms;

import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;

public abstract class MoistureGenerator extends Contract {

    public final Key<Double> faceMoisture = new Key<>(facesPrefix + "HAS_MOISTURE", Double.class);

    @Override
    public Constraints getContract() {
        return new Constraints(asSet(faces,seed), asSet(faceMoisture));
    }
}
