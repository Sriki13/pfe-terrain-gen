package pfe.terrain.gen.algo.generator;

import pfe.terrain.gen.algo.Context;

import java.util.Map;

public interface Generator {

    String generate();
    int getId();
    void setParams(Map<String,Object> map);
}
