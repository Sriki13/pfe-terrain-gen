package pfe.terrain.gen.algo.algorithms;

import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class InitializationGenerator extends Contract {

    protected Key<Integer> seedParam = new Key<>("size", Integer.class);
    protected Key<Integer> sizeParam = new Key<>("seed", Integer.class);


    @Override
    public Constraints getContract() {
        return new Constraints(Collections.emptySet(),
                Stream.of(seed, size).collect(Collectors.toSet()));
    }

    @Override
    public Set<Key> getRequestedParameters() {
        return Stream.of(seed, size).collect(Collectors.toSet());
    }
}
