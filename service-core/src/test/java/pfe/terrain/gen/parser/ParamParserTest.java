package pfe.terrain.gen.parser;

import org.junit.Test;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.constraints.AdditionalConstraint;
import pfe.terrain.gen.constraints.ContractOrder.ContractOrder;
import pfe.terrain.gen.parser.ParamParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ParamParserTest {


    private class NbPointsContract extends Contract{
        private String name;

        public NbPointsContract(){
            this.name = "salut";
        }

        public NbPointsContract(String name){
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }


        @Override
        public Constraints getContract() {
            return null;
        }

        @Override
        public String getDescription() {
            return null;
        }

        @Override
        public void execute(TerrainMap map, Context context) {

        }

        @Override
        public Set<Param> getRequestedParameters() {
            return asParamSet(new Param("nbPoints", Integer.class, "", "", 0, ""));
        }
    }

    @Test
    public void readTest() throws Exception {
        String path = this.getClass().getClassLoader().getResource("context.json").getPath();

        ParamParser initializer = new ParamParser(path);
        assertEquals("{ \"context\" : {\"nbPoints\" : 4000}}",initializer.getContextString());
    }

    @Test
    public void contextTest() throws Exception {
        String path = this.getClass().getClassLoader().getResource("context.json").getPath();

        ParamParser initializer = new ParamParser(path);

        Context context = initializer.getContext(Arrays.asList(new NbPointsContract()));

        assertEquals(4000, context.getParamOrDefault(new Param("nbPoints", Integer.class, "", "", 0, "")));
    }

    @Test
    public void constraintsTest(){
        String path = this.getClass().getClassLoader().getResource("contextWithConstraints.json").getPath();

        ParamParser initializer = new ParamParser(path);
        List<Contract> contracts = Arrays.asList(new NbPointsContract("A"),new NbPointsContract("B"));

        List<AdditionalConstraint> constraints = initializer.getConstraints(contracts);

        assertEquals(1,constraints.size());
        assertTrue(constraints.get(0) instanceof ContractOrder);
    }

    @Test
    public void noConstraintsTest(){
        String path = this.getClass().getClassLoader().getResource("contextWithConstraints.json").getPath();

        ParamParser initializer = new ParamParser(path);

        List<AdditionalConstraint> constraints = initializer.getConstraints(new ArrayList<>());

        assertEquals(0,constraints.size());
    }


}
