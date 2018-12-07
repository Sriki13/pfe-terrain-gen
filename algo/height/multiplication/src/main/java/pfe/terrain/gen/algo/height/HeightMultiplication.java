package pfe.terrain.gen.algo.height;

import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.constraints.key.SerializableKey;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.island.geometry.CoordSet;
import pfe.terrain.gen.algo.types.DoubleType;

import java.util.Set;

public class HeightMultiplication extends Contract {

    private Param<Integer> FACTOR_KEY = Param.generatePositiveIntegerParam("Val Multiplication", 10,
            "Multiplies heights to increase height differences", 1, "Height multiplication factor");

    public static final Key<DoubleType> VERTEX_HEIGHT_KEY =
            new SerializableKey<>(VERTICES_PREFIX + "HEIGHT", "height", DoubleType.class);


    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(FACTOR_KEY);
    }

    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(VERTICES),
                asKeySet(),
                asKeySet(VERTEX_HEIGHT_KEY)
        );
    }

    @Override
    public void execute(TerrainMap map, Context context) {
        Integer factor = context.getParamOrDefault(FACTOR_KEY);
        CoordSet vertices = map.getProperty(VERTICES);
        for (Coord coord : vertices) {
            DoubleType height = coord.getProperty(VERTEX_HEIGHT_KEY);
            coord.putProperty(VERTEX_HEIGHT_KEY, new DoubleType(height.value * factor));
        }
    }
}
