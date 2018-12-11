package pfe.terrain.factory;

import org.junit.Test;
import pfe.terrain.factory.exception.ParsingException;
import pfe.terrain.factory.parser.AlgoCompatibilityChange;
import pfe.terrain.factory.parser.JsonParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AlgoCompatibilityparsingTest {

    @Test
    public void parsingTest() throws Exception{
        JsonParser parser = new JsonParser();

        AlgoCompatibilityChange change = parser.getAlgoCompatibility("{\"AlgoNames\" : [\"test\",\"ok\",\"oui\"],\"compatibility\" : 5}");

        assertTrue(change.getAlgoNames().contains("test"));
        assertTrue(change.getAlgoNames().contains("ok"));
        assertTrue(change.getAlgoNames().contains("oui"));

        assertEquals(change.getCompateNumber(),5);
    }

    @Test (expected = ParsingException.class)
    public void failParsingTest() throws Exception{
        JsonParser parser = new JsonParser();

        parser.getAlgoCompatibility("azeazeaz");
    }
}
