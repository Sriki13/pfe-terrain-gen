import org.junit.Assert;
import org.junit.Test;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.generatorService.parser.LazyContext;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class LazyContextTest {

    private LazyContext context;

    @Test
    public void getValTest() throws Exception{
        Map<String,Object> map = new HashMap<>();

        map.put("salut",12);

        context = new LazyContext(map);

        assertEquals(context.getProperty(new Key<>("salut",Object.class)),12);
    }
}
