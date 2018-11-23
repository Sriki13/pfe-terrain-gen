package pfe.terrain.gen;

import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.commands.GetFileCommand;
import pfe.terrain.gen.algo.exception.WrongArgsException;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class ReadFileCommandTest {

    private GetFileCommand command;

    @Before
    public void init(){
        this.command = new GetFileCommand();
    }

    @Test
    public void readTest() throws Exception {
        File file = new File(ExecuteCommandTest.class.getResource("/param.json").getFile());

        String content = command.execute("-f",file.getCanonicalPath());

        assertEquals("{\"salut\": 12}",content);
    }

    @Test(expected = WrongArgsException.class)
    public void failReadTest() throws Exception{
        command.execute("-f","azeaze");
    }
}
