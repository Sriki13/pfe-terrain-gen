package pfe.terrain.gen.algo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.exception.DuplicateKeyException;
import pfe.terrain.gen.algo.exception.InvalidAlgorithmParameters;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.MapContext;

import java.util.*;

public class MapContextTest {

    Key<Integer> mountain = new Key<>("mountains",Integer.class);
    Key<String> terrain = new Key<>("terrain",String.class);
    Key<Double> wave = new Key<>("wave",Double.class);
    Key<Boolean> hill = new Key<>("hill",Boolean.class);

    private Contract contract = new Contract() {
        @Override
        public Constraints getContract() {
            return null;
        }

        @Override
        public void execute(IslandMap map, Context context) throws InvalidAlgorithmParameters, DuplicateKeyException, NoSuchKeyException, KeyTypeMismatch {

        }

        @Override
        public Set<Key> getRequestedParameters() {
            Set<Key> keys = new HashSet<>();

            keys.add(mountain);
            keys.add(terrain);
            keys.add(wave);
            keys.add(hill);

            return keys;
        }
    };

    @Test
    public void paramFromMap() throws Exception{
        Map<String,Object> map = new HashMap<>();

        map.put("hill",false);
        map.put("mountains",12.0);
        map.put("wave",10.0);
        map.put("terrain","we");

        MapContext context = new MapContext(map, Arrays.asList(this.contract));

        assertEquals(false,context.getProperty(hill));
        assertEquals(new Integer(12),context.getProperty(mountain));
        assertEquals(new Double(10.0),context.getProperty(wave));
        assertEquals("we",context.getProperty(terrain));
    }

    @Test
    public void missingVal() throws Exception{
        Map<String,Object> map = new HashMap<>();

        map.put("hill",false);
        map.put("mountains",12.0);

        MapContext context = new MapContext(map,Arrays.asList(this.contract));

        assertEquals(false,context.getProperty(hill));
        assertEquals(new Integer(12),context.getProperty(mountain));
    }

    @Test(expected = NoSuchKeyException.class)
    public void wrongType() throws Exception{
        Map<String,Object> map = new HashMap<>();

        map.put("hill",13);
        map.put("mountains",12);

        MapContext context = new MapContext(map,Arrays.asList(this.contract));

        assertNull(context.getProperty(hill));

    }

    @Test
    public void wrongNumberType() throws Exception{
        Map<String,Object> map = new HashMap<>();

        map.put("mountains",13.0);

        MapContext context = new MapContext(map,Arrays.asList(this.contract));

    }
}
