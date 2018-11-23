package pfe.terrain.gen;

import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.generator.Generator;
import pfe.terrain.gen.commands.ExecuteCommand;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ExecuteCommandTest {

    private ExecuteCommand command;

    @Before
    public void init(){
        this.command = new ExecuteCommand(new Generator() {
            Context map;

            @Override
            public String generate() {
                return "test";
            }

            @Override
            public int getId() {
                return 0;
            }

            @Override
            public void setParams(Context map) {
                this.map = map;
            }

            @Override
            public List<Contract> getContracts() {
                return new ArrayList<>();
            }
        });
    }

    @Test
    public void execTest(){
        assertEquals("test",this.command.execute());
    }

    @Test
    public void fromFile() throws Exception{
        File file = new File(ExecuteCommandTest.class.getResource("/param.json").getFile());

        String content = command.execute("executeAll","-f",file.getCanonicalPath());

        assertEquals("test",content);
    }

    @Test
    public void fromCli() throws Exception {
        String content = command.execute("executeAll","-c","{\"salut\" : 12}");

        assertEquals("test",content);
    }


}
