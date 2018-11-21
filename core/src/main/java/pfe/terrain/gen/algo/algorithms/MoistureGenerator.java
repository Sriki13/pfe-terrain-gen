package pfe.terrain.gen.algo.algorithms;

import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class MoistureGenerator extends Contract {

    public final Key<Double> faceMoisture = new Key<>(facesPrefix + "HAS_MOISTURE", Double.class);

    @Override
    public Constraints getContract() {
        return new Constraints(Stream.of(faces,seed).collect(Collectors.toSet()),
                Stream.of(faceMoisture).collect(Collectors.toSet()));
    }
}
