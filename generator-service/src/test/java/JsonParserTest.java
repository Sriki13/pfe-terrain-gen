import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.Param;
import pfe.terrain.generatorService.holder.Parameter;
import pfe.terrain.generatorService.parser.JsonParser;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class JsonParserTest {

    private JsonParser parser;

    @Before
    public void init(){
        parser = new JsonParser();
    }

    @Test
    public void keyParsingTest(){
        String json = parser.parseKeys(Arrays.asList(new Parameter(
                new Param<>("test", Integer.class, "", "", 1, ""), "salu", "salu", "")));

        Object[] maps = new Gson().fromJson(json,Object[].class);

        assertEquals(1,maps.length);
    }
}
