package pfe.terrain.generatorService;

import pfe.terrain.generatorService.controller.ServiceController;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GenMap {

    public static void main(String[] args) throws Exception {
        ServiceController controller = new ServiceController();
        String filename = null;
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-f":
                    String context = new String(Files.readAllBytes(Paths.get(args[i + 1])), StandardCharsets.UTF_8);
                    controller.setContext(context);
                    break;
                case "-o":
                    filename = args[i + 1];
                    break;
                default:
                    System.out.println("Unsupported arg: " + args[i]);
            }
        }
        String map = controller.execute();
        if (filename == null) {
            System.out.println(map);
        } else {
            System.out.println("Writing map to file...");
            long start = System.nanoTime();
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filename)));
            writer.write(map);
            writer.close();
            long end = System.nanoTime();
            System.out.println("Done in " + (end - start) / 1000 + " microseconds.");
        }
    }

}
