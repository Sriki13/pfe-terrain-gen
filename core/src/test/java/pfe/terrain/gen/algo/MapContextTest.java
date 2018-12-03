package pfe.terrain.gen.algo;

import org.junit.Test;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.context.Context;
import pfe.terrain.gen.algo.context.MapContext;
import pfe.terrain.gen.algo.island.IslandMap;
import pfe.terrain.gen.algo.key.Param;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class MapContextTest {

    Param<Integer> mountain = new Param<>("mountains", Integer.class, "a", "b", 1, "");
    Param<String> terrain = new Param<>("terrain", String.class, "c", "d", "kapoue", "");
    Param<Double> wave = new Param<>("wave", Double.class, "f", "f", 3.4, "");
    Param<Boolean> hill = new Param<>("hill", Boolean.class, "x", "z", true, "");

    private Contract contract = new Contract() {
        @Override
        public Constraints getContract() {
            return null;
        }

        @Override
        public void execute(IslandMap map, Context context) {

        }

        @Override
        public Set<Param> getRequestedParameters() {
            return asParamSet(mountain, terrain, wave, hill);
        }
    };

    @Test
    public void paramFromMap() throws Exception {
        Map<String, Object> map = new HashMap<>();

        map.put("hill", false);
        map.put("mountains", 12.0);
        map.put("wave", 10.0);
        map.put("terrain", "we");

        MapContext context = new MapContext(map, Arrays.asList(this.contract));

        assertEquals(false, context.getParamOrDefault(hill));
        assertEquals(new Integer(12), context.getParamOrDefault(mountain));
        assertEquals(new Double(10.0), context.getParamOrDefault(wave));
        assertEquals("we", context.getParamOrDefault(terrain));
    }

    @Test
    public void missingVal() throws Exception {
        Map<String, Object> map = new HashMap<>();

        map.put("hill", false);
        map.put("mountains", 12.0);

        MapContext context = new MapContext(map, Arrays.asList(this.contract));

        assertEquals(false, context.getParamOrDefault(hill));
        assertEquals(new Integer(12), context.getParamOrDefault(mountain));
    }

    @Test
    public void defaultType() throws Exception {
        Map<String, Object> map = new HashMap<>();

        map.put("hill", 13);
        map.put("mountains", 12);

        MapContext context = new MapContext(map, Arrays.asList(this.contract));

        assertEquals(context.getParamOrDefault(hill), true);

    }

    @Test
    public void wrongNumberType() throws Exception {
        Map<String, Object> map = new HashMap<>();

        map.put("mountains", 13.0);

        MapContext context = new MapContext(map, Arrays.asList(this.contract));

    }
}
