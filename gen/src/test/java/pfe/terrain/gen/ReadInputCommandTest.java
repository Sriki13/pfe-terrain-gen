package pfe.terrain.gen;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.commands.GetInputCommand;
import pfe.terrain.gen.exception.WrongArgsException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

public class ReadInputCommandTest {

    private GetInputCommand command;

    @Before
    public void init(){
        command = new GetInputCommand();
    }

    @Test
    public void readTest() throws Exception{
        String data = "{\"salut\": 12}";
        InputStream testInput = new ByteArrayInputStream( data.getBytes("UTF-8") );

        System.setIn(testInput);

        String content = command.execute("-i");

        assertEquals(data,content);

    }

    @Test (expected = WrongArgsException.class)
    public void failReadTest() throws Exception{
        System.setIn(null);
        String content = command.execute("-i");
    }
}
