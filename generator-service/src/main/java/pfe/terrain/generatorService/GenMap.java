package pfe.terrain.generatorService;

import pfe.terrain.generatorService.controller.ServiceController;

public class GenMap {
    public static void main(String[] args)  throws Exception{
        ServiceController controller = new ServiceController();

        System.out.println(controller.execute());
    }
}
