package pfe.terrain.generatorService;

import pfe.terrain.generatorService.controller.ServiceController;

public class GraphMain {

    public static void main(String[] args) throws Exception {
        System.out.println("Generating graph...");
        ServiceController controller = new ServiceController();
        controller.generateGraphImage();
        System.out.println("Graph generated as graph.png");
    }

}
