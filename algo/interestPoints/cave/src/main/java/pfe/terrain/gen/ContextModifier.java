package pfe.terrain.gen;

import pfe.terrain.gen.algo.constraints.context.Context;

public interface ContextModifier<T> {

    void modify(Context context);
    void modify(Context context, double factor);

}
