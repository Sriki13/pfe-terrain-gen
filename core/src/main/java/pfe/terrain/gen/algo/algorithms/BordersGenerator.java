//package pfe.terrain.gen.algo.algorithms;
//
//import pfe.terrain.gen.algo.Key;
//import pfe.terrain.gen.algo.constraints.Constraints;
//import pfe.terrain.gen.algo.constraints.Contract;
//import pfe.terrain.gen.algo.geometry.BordersSet;
//
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
////TODO FIX
//public interface BordersGenerator extends Contract {
//
//    @Override
//    default Constraints getContract() {
//        return new Constraints(
//                Stream.of(
//                        new Key<>("VERTICES", Void.class),
//                        new Key<>("EDGES", Void.class),
//                        new Key<>("FACES", Void.class)).collect(Collectors.toSet()),
//                Stream.of(
//                        new Key<>("BORDERS", BordersSet.class)).collect(Collectors.toSet())
//        );
//    }
//
//}
