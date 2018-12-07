package pfe.terrain.gen.algo;

import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;

import java.util.List;

public interface Generator {

    String generate(boolean diffOnly);

    void setParams(Context map);

    List<Contract> getContracts();
}
