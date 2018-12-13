package pfe.terrain.gen;

import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.Param;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ContextModifierDouble implements ContextModifier<Double> {
    private Param<Double> key;
    private double val;

    private Logger logger = Logger.getLogger("modifier");

    public ContextModifierDouble(Param<Double> key, Double val){
        this.key = key;
        this.val = val;
    }

    @Override
    public void modify(Context context) {
        try {
            Double oldVal = context.getParamOrDefault(this.key);
            context.putParam(this.key, oldVal + this.val);
        } catch(Exception e) {
            logger.log(Level.INFO,"cannot modify param :" + this.key.getLabel());
        }
    }

    @Override
    public void modify(Context context, double factor) {

    }
}
