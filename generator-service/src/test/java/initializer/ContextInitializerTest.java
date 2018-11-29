package initializer;

import org.junit.Test;
import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.Param;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.generatorService.initializer.ContextInitializer;

import java.util.Arrays;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class ContextInitializerTest {


    private class NbPointsContract extends Contract{

        @Override
        public Constraints getContract() {
            return null;
        }

        @Override
        public void execute(IslandMap map, Context context) {

        }

        @Override
        public Set<Param> getRequestedParameters() {
            return asParamSet(new Param("nbPoints",Integer.class,"","",0));
        }
    }

    @Test
    public void readTest() throws Exception{
        String path = this.getClass().getClassLoader().getResource("context.json").getPath();

        ContextInitializer initializer = new ContextInitializer(path);

        assertEquals("{\"nbPoints\" : 4000}",initializer.getContextString());
    }

    @Test
    public void contextTest() throws Exception{
        String path = this.getClass().getClassLoader().getResource("context.json").getPath();

        ContextInitializer initializer = new ContextInitializer(path);

        Context context = initializer.getContext(Arrays.asList(new NbPointsContract()));

        assertEquals(4000,context.getParamOrDefault(new Param("nbPoints",Integer.class,"","",0)));
    }



}
