package pfe.terrain.gen.algo.constraints.context;

import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.exception.WrongTypeException;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MapContext extends Context {

    public MapContext(){

    }

    public MapContext(Map<String, Object> map, List<Contract> contracts) {

        Set<Param> contractParams = new HashSet<>();

        for (Contract contract : contracts) {
            contractParams.addAll(contract.getRequestedParameters());
        }

        for (String name : map.keySet()) {
            boolean found = false;
            for (Param key : contractParams) {
                if (key.getId().equals(name)) {
                    found = true;
                    try {
                        this.putParam(key, tryConvert(map.get(name), key.getType()));
                        break;
                    } catch (WrongTypeException e) {
                        System.err.println("property " + key.getId() + " is wrong, cannot be loaded");
                    }
                }
            }
            if (!found) {
                System.out.println("No contract uses the parameter " + name);
            }
        }
    }

    public Object tryConvert(Object val, Class type) throws WrongTypeException {
        try {
            if (type == Integer.class) {
                return ((Double) val).intValue();
            }

            return type.cast(val);
        } catch (Exception e) {
            throw new WrongTypeException();
        }
    }
}
