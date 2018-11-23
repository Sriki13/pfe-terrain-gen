package pfe.terrain.gen;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.generator.Generator;
import pfe.terrain.gen.commands.ExecuteCommand;

import java.io.File;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ExecuteCommandTest {

    private ExecuteCommand command;

    @Before
    public void init(){
        this.command = new ExecuteCommand(new Generator() {
            Map<String,Object> map;

            @Override
            public String generate() {
                return "test";
            }

            @Override
            public int getId() {
                return 0;
            }

            @Override
            public void setParams(Map<String, Object> map) {
                this.map = map;
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

        String content = command.execute("execute","-f",file.getCanonicalPath());

        assertEquals("test",content);
    }


}
