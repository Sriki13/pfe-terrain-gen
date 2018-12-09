package pfe.terrain.factory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pfe.terrain.factory.exception.MissingKeyException;
import pfe.terrain.factory.parser.JsonCompoParser;

import java.util.List;

public class CompoParserTest {
    private JsonCompoParser parser;

    @Before
    public void init() throws Exception{
        this.parser = new JsonCompoParser("{\"name\" : \"salut\" , \"context\" : {\"test\" : false} , \"algorithm\" : [\"test\",\"we\"]}");
    }

    @Test
    public void getTest(){
        Assert.assertEquals("{\"test\":false}",parser.getContext());
        Assert.assertEquals("salut",parser.getName());

        List<String> algos = parser.getAlgoName();

        Assert.assertTrue(algos.contains("test"));
        Assert.assertTrue(algos.contains("we"));
    }

    @Test(expected = MissingKeyException.class)
    public void failTest() throws Exception{
        this.parser = new JsonCompoParser("{\"name\" : 12 , \"context\" : \"test\" , \"algorithm\" : [\"test\",\"we\"]}");

    }

    @Test(expected = MissingKeyException.class)
    public void noJsonTest() throws Exception{
        new JsonCompoParser("azaza");
    }

    @Test(expected = MissingKeyException.class)
    public void keyFailTest() throws Exception{
        this.parser = new JsonCompoParser("{\"nameeee\" : \"azaze\" , \"context\" : \"test\" , \"algorithm\" : [\"test\",\"we\"]}");

    }
}

