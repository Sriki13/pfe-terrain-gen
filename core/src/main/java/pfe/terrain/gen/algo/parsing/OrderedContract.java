package pfe.terrain.gen.algo.parsing;

public class OrderedContract {
    private String name;
    private int order;

    public OrderedContract(){

    }

    public OrderedContract(String name, int order) {
        this.name = name;
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public int getOrder() {
        return order;
    }
}
