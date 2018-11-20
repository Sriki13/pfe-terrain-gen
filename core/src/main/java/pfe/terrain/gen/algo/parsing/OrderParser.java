package pfe.terrain.gen.algo.parsing;

import com.google.gson.Gson;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.exception.OrderParsingException;

import java.util.ArrayList;
import java.util.List;

public class OrderParser {

    public String writeList(List<Contract> contracts){
        List<OrderedContract> orderedContracts = new ArrayList<>();

        int order = 0;
        for(Contract contract : contracts){
            orderedContracts.add(new OrderedContract(contract.getName(),order));
            order++;
        }

        Gson gson = new Gson();

        return gson.toJson(orderedContracts);
    }

    public List<OrderedContract> getList(String json) throws OrderParsingException{
        try {
            Gson gson = new Gson();

            OrderedContract[] contracts = gson.fromJson(json, OrderedContract[].class);

            List<OrderedContract> res = new ArrayList<>();
            for (OrderedContract contract : contracts) {
                res.add(contract);
            }

            return res;
        } catch (Exception e){
            throw new OrderParsingException();
        }


    }

}
