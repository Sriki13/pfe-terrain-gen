package pfe.terrain.gen.algo.height;

import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.SerializableKey;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.geometry.*;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HeightMultiplication extends Contract {

    private Key<Integer> factorKey = new Key<>("heightMultiplication", Integer.class);

    public static final Key<DoubleType> vertexHeightKey =
            new SerializableKey<>(verticesPrefix + "HEIGHT", "height", DoubleType.class);


    @Override
    public Set<Key> getRequestedParameters() {
        return asSet(factorKey);
    }

    @Override
    public Constraints getContract() {
        return new Constraints(
                asSet(vertices),
                asSet(),
                asSet(vertexHeightKey)
        );
    }

    @Override
    public void execute(IslandMap map, Context context)
            throws DuplicateKeyException, NoSuchKeyException, KeyTypeMismatch {

        Integer factor = context.getPropertyOrDefault(factorKey, 2);

        CoordSet vertices = map.getVertices();

        for(Coord coord : vertices){
            DoubleType height = coord.getProperty(vertexHeightKey);

            coord.putProperty(vertexHeightKey,new DoubleType(height.value * factor));
        }

    }
}
