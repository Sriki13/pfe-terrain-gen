package pfe.terrain.gen.algo.height;

import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.context.Context;
import pfe.terrain.gen.algo.geometry.Coord;
import pfe.terrain.gen.algo.geometry.CoordSet;
import pfe.terrain.gen.algo.island.IslandMap;
import pfe.terrain.gen.algo.key.Key;
import pfe.terrain.gen.algo.key.Param;
import pfe.terrain.gen.algo.key.SerializableKey;
import pfe.terrain.gen.algo.types.DoubleType;

import java.util.Set;

public class HeightMultiplication extends Contract {

    private Param<Integer> factorKey = Param.generatePositiveIntegerParam("Val Multiplication", 10,
            "Multiplies heights to increase height differences", 1, "Height multiplication factor");

    public static final Key<DoubleType> vertexHeightKey =
            new SerializableKey<>(verticesPrefix + "HEIGHT", "height", DoubleType.class);


    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(factorKey);
    }

    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(vertices),
                asKeySet(),
                asKeySet(vertexHeightKey)
        );
    }

    @Override
    public void execute(IslandMap map, Context context) {

        Integer factor = context.getParamOrDefault(factorKey);

        CoordSet vertices = map.getVertices();

        for (Coord coord : vertices) {
            DoubleType height = coord.getProperty(vertexHeightKey);

            coord.putProperty(vertexHeightKey, new DoubleType(height.value * factor));
        }

    }
}
