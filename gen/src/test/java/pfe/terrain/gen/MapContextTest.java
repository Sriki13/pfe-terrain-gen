package pfe.terrain.gen;

import org.junit.Assert;
import static org.junit.Assert.assertEquals;
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
import pfe.terrain.gen.contextParser.MapContext;
import pfe.terrain.gen.exception.WrongTypeException;

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
        map.put("mountains",12);
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
        map.put("mountains",12);

        MapContext context = new MapContext(map,Arrays.asList(this.contract));

        assertEquals(false,context.getProperty(hill));
        assertEquals(new Integer(12),context.getProperty(mountain));
    }

    @Test(expected = WrongTypeException.class)
    public void wrongType() throws Exception{
        Map<String,Object> map = new HashMap<>();

        map.put("hill",13);
        map.put("mountains",12);

        MapContext context = new MapContext(map,Arrays.asList(this.contract));

    }

    @Test(expected = WrongTypeException.class)
    public void wrongNumberType() throws Exception{
        Map<String,Object> map = new HashMap<>();

        map.put("wave",13);

        MapContext context = new MapContext(map,Arrays.asList(this.contract));

    }
}
