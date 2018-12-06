package pfe.terrain.gen.algo.reflection;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import pfe.terrain.gen.algo.constraints.Contract;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ContractReflection {

    public List<Contract> getContracts(){
        List<Contract> contracts = new ArrayList<>();
        try {
            Reflections reflections = new Reflections("pfe.terrain.gen", new SubTypesScanner(false));
            Set<Class<? extends Contract>> subTypes = reflections.getSubTypesOf(Contract.class);

            for (Class cl : subTypes) {
                try {
                    contracts.add((Contract) cl.newInstance());
                } catch (InstantiationException e) {
                    System.err.println(cl.getName() + " was not instantiated");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contracts;
    }
}
