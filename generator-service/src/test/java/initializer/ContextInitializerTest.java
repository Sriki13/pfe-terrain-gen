package initializer;

import org.junit.Assert;
import org.junit.Test;
import pfe.terrain.generatorService.initializer.ContextInitializer;

import static org.junit.Assert.assertEquals;

public class ContextInitializerTest {


    @Test
    public void readTest() throws Exception{
        String path = this.getClass().getClassLoader().getResource("context.json").getPath();

        ContextInitializer initializer = new ContextInitializer(path);

        assertEquals("{\"nbPoints\" : 4000}",initializer.getContextString());
    }



}
