package pfe.terrain.gen;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.exception.WrongArgsException;
import pfe.terrain.gen.commands.GetParamCLICommand;

public class GetParamCliCommandTest {

    private GetParamCLICommand command;

    @Before
    public void init(){
        this.command = new GetParamCLICommand();
    }

    @Test
    public void getStringTest() throws Exception{
        String content = this.command.execute(this.command.getName(),"salut");

        Assert.assertEquals("salut",content);
    }

    @Test(expected = WrongArgsException.class)
    public void missingParam() throws Exception {
        this.command.execute(this.command.getName());
    }
}
