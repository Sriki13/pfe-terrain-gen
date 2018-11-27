package pfe.terrain.generatorService;

import pfe.terrain.generatorService.controller.ServiceController;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GenMap {

    public static void main(String[] args)  throws Exception{
        ServiceController controller = new ServiceController();
        if (args.length == 2 && args[0].equals("-f")) {
            String context = new String(Files.readAllBytes(Paths.get(args[1])), StandardCharsets.UTF_8);
            controller.setContext(context);
        }
        System.out.println(controller.execute());
    }

}
