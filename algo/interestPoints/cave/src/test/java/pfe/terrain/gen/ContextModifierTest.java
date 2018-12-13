package pfe.terrain.gen;

import org.junit.Test;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Param;

import static org.junit.Assert.assertEquals;

public class ContextModifierTest {



    @Test
    public void contextModifTest(){
        Param<Integer> integerParam = Param.generatePositiveIntegerParam("int",100,"int",9,"int");
        Param<Double> doubleParam = Param.generateDefaultDoubleParam("double","double",0.67,"dobule");

        ContextModifier modifierInt = new ContextModifierInteger(integerParam,60);
        ContextModifier modifierDouble = new ContextModifierDouble(doubleParam,0.09);

        Context context = new Context();
        context.putParam(doubleParam,0.6);

        modifierDouble.modify(context);
        modifierInt.modify(context);

        assertEquals(69,context.getParamOrDefault(integerParam).intValue());
        assertEquals(0.69,context.getParamOrDefault(doubleParam).doubleValue(),0.0);
    }
}
