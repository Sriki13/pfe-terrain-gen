package pfe.terrain.gen;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.CommandConstants;
import pfe.terrain.gen.algo.generator.Generator;
import pfe.terrain.gen.commands.Command;

import java.util.Map;

public class CommandParserTest {

    private CommandParser parser;

    @Before
    public void init(){
        Generator gen = new Generator() {
            @Override
            public String generate() {
                return "salut";
            }

            @Override
            public int getId() {
                return 0;
            }

            @Override
            public void setParams(Map<String, Object> map) {

            }
        };
        parser = new CommandParser(gen);
    }

    @Test
    public void idTest(){
        Assert.assertEquals("0",parser.execute(CommandConstants.getId));
    }

    @Test
    public void executeTest(){
        Assert.assertEquals("salut",parser.execute(CommandConstants.exec));
    }

    @Test
    public void helpTest(){
        Assert.assertNotNull(parser.execute(CommandConstants.desc));
    }

    @Test
    public void noParamTest(){
        Assert.assertNotNull(parser.execute("wiw"));
    }

}
