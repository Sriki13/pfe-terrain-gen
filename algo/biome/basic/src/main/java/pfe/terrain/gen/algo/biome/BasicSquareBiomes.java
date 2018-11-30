package pfe.terrain.gen.algo.biome;

import pfe.terrain.gen.algo.*;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.geometry.Face;
import pfe.terrain.gen.algo.types.BooleanType;

import java.util.HashSet;
import java.util.Set;

public class BasicSquareBiomes extends Contract {

    public final Key<BooleanType> faceBorderKey =
            new Key<>(facesPrefix + "IS_BORDER", BooleanType.class);
    public final Key<Biome> faceBiomeKey =
            new SerializableKey<>(facesPrefix + "BIOME", "biome", Biome.class);

    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(faceBorderKey, faces),
                asKeySet(faceBiomeKey)
        );
    }

    @Override
    public void execute(IslandMap map, Context context) {
        Set<Face> borderFaces = new HashSet<>();
        for (Face face : map.getFaces()) {
            if (face.getProperty(faceBorderKey).value) {
                borderFaces.add(face);
            }
        }
        for (Face face : map.getFaces()) {
            if (borderFaces.contains(face)) {
                face.putProperty(faceBiomeKey, Biome.OCEAN);
            } else {
                face.putProperty(faceBiomeKey, Biome.SUB_TROPICAL_DESERT);
            }
        }
    }

}
