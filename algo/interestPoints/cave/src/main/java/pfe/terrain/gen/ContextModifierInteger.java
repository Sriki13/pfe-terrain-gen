package pfe.terrain.gen;

import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Param;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ContextModifierInteger implements ContextModifier<Integer> {
    private Param<Integer> key;
    private int val;

    private Logger logger = Logger.getLogger("modifier");


    public ContextModifierInteger(Param<Integer> key, Integer val){
        this.key = key;
        this.val = val;
    }

    @Override
    public void modify(Context context) {
        try {
            Integer oldVal = context.getParamOrDefault(this.key);
            context.putParam(this.key, oldVal + this.val);
        } catch(Exception e){
            logger.log(Level.INFO,"cannot modify param :" + this.key.getLabel());
        }
    }

    @Override
    public void modify(Context context, double factor) {

    }
}
