package pfe.terrain.gen.contextParser;

import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.exception.WrongTypeException;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MapContext extends Context {

    public MapContext(Map<String,Object> map, List<Contract> contracts) throws WrongTypeException{

        Set<Key> contractParams = new HashSet<>();

        for(Contract contract : contracts){
            contractParams.addAll(contract.getRequestedParameters());
        }

        for(String name : map.keySet()){

            for(Key key : contractParams){
                if(key.getId().equals(name)){

                    this.putProperty(key,tryConvertion(map.get(name),key.getType()));
                    break;
                }
            }
        }
    }

    public Object tryConvertion(Object val,Class type) throws WrongTypeException{
        try {
            if (type == Integer.class) {
                return ((Double) val).intValue();
            }

            return type.cast(val);
        } catch (Exception e){
            throw new WrongTypeException();
        }
    }
}
