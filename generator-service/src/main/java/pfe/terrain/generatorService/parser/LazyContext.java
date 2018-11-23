package pfe.terrain.generatorService.parser;

import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.parsing.ContextParser;

import java.util.Map;

public class LazyContext  extends Context {

    public LazyContext(Map<String,Object> map){
        for(String key : map.keySet()){
            this.putProperty(new Key<>(key,Object.class),map.get(key));
        }
    }
}
