package pfe.terrain.gen;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.generator.Generator;
import pfe.terrain.gen.algo.geometry.CoordSet;
import pfe.terrain.gen.algo.parsing.OrderParser;
import pfe.terrain.gen.algo.parsing.OrderedContract;
import pfe.terrain.gen.export.JSONExporter;

import java.io.InputStream;
import java.util.*;

public class MapGenerator implements Generator {

    private List<OrderedContract> orderedContracts;
    private List<Contract> contracts;
    private IslandMap islandMap;
    private int id;
    private Context context;

    public MapGenerator(){
        this.islandMap = new IslandMap();

        this.context = new Context();

        this.contracts = new ArrayList<>();
        this.getContractOrder();
        this.instantiateContracts();

    }

    public String generate(){

        this.execute();
        try{
            JSONExporter exporter = new JSONExporter();
            return exporter.export(this.islandMap).toString();
        } catch (Exception e){
            return e.getMessage();
        }
    }

    @Override
    public int getId() {
        return id;
    }

    public void getContractOrder(){
        try {
            StringBuilder result = new StringBuilder();
            InputStream stream = AppGen.class.getResourceAsStream("/order.json");
            Scanner scanner = new Scanner(stream);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                result.append(line).append("\n");
            }

            scanner.close();
            OrderParser parser = new OrderParser();
            this.orderedContracts = parser.getList(result.toString());
            this.id = result.toString().hashCode();
            this.orderedContracts.sort((o1, o2) -> {
                if(o1 == o2) return 0;
                if(o1.getOrder() > o2.getOrder()) return 1;
                else return -1;
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void instantiateContracts(){

        try {
            Reflections reflections = new Reflections("pfe.terrain.gen", new SubTypesScanner(false));
            Set<Class<? extends Contract>> subTypes = reflections.getSubTypesOf(Contract.class);

            for (Class cl : subTypes) {
                try {
                    contracts.add((Contract) cl.newInstance());
                } catch (Exception e){
                    System.err.println(e.getMessage());
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void execute(){
        for(OrderedContract ctr : orderedContracts){
            try{
                this.executeByName(ctr.getName());
            } catch (Exception e){
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void executeByName(String name) throws Exception{
        for(Contract contract : contracts){
            if(contract.getName().equals(name)){
                contract.execute(islandMap,this.context);
            }
        }
    }

}
