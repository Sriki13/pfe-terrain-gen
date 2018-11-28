package pfe.terrain.gen.algo.generator;

import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.constraints.Contract;

import java.util.List;

public interface Generator {

    String generate() throws Exception;

    void setParams(Context map);

    List<Contract> getContracts();
}
