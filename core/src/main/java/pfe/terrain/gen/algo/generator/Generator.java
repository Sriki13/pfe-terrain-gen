package pfe.terrain.gen.algo.generator;

import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.context.Context;

import java.util.List;

public interface Generator {

    String generate() throws Exception;

    void setParams(Context map);

    List<Contract> getContracts();
}
