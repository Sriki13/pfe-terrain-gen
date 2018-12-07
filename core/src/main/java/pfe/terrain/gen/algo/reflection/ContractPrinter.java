package pfe.terrain.gen.algo.reflection;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import pfe.terrain.gen.algo.constraints.Contract;

import java.util.List;
import java.util.Map;

public class ContractPrinter {

    public static void main(String[] args) {
        ContractReflection reflection = new ContractReflection();
        List<Contract> contracts = reflection.getContracts();
        Gson gson = new Gson();
        JsonArray array = new JsonArray();

        for(Contract contract : contracts){
            JsonElement elem = gson.toJsonTree(gson.fromJson(contract.toJson(), Map.class),Map.class);

            array.add(elem);
        }

        System.out.println(gson.toJson(array));
    }
}
