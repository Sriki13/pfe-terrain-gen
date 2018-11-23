package pfe.terrain.gen.algo;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.parsing.ContextParser;

import java.util.Map;

public class ContextParserTest {

    private ContextParser parser;

    private String json = "{\"salut\" : 10 , \"test\" : \"WOW\",\"bool\" : false}";

    @Before
    public void init(){
        parser = new ContextParser(json);
    }

    @Test
    public void getMapTest(){
        Map<String,Object> map = parser.getMap();

        Assert.assertEquals(10.0,map.get("salut"));
        Assert.assertEquals("WOW",map.get("test"));
        Assert.assertEquals(false,map.get("bool"));
    }
}
