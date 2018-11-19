package pfe.terrain.gen.algo.algorithms;

import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.geometry.FaceSet;
import pfe.terrain.gen.algo.geometry.MoistureMap;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface MoistureGenerator extends Contract {

    @Override
    default Constraints getContract() {
        return new Constraints(Stream.of(new Key<>("FACES", FaceSet.class)).collect(Collectors.toSet()),
                Stream.of(new Key<>("FACEMOISTURE", MoistureMap.class)).collect(Collectors.toSet()));
    }

}
