package pfe.terrain.generatorService;

import pfe.terrain.gen.algo.CommandConstants;
import pfe.terrain.gen.algo.generator.Generator;
import pfe.terrain.generatorService.exception.CannotUseGeneratorException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BashGenerator implements Generator {

    private String jarPath;
    private int id;
    private boolean isSet = false;

    public BashGenerator(String jarPath) throws CannotUseGeneratorException {
        this.jarPath = jarPath;

        this.id = this.getId();

        if (!isSet) {
            throw new CannotUseGeneratorException();
        }
    }

    @Override
    public String generate() {
        try {
            return this.jarRunner(CommandConstants.exec);
        } catch (Exception e) {
            return "GENERATOR KO";
        }
    }

    @Override
    public int getId() {
        if (isSet) {
            return this.id;
        }

        try {
            String idString = this.jarRunner(CommandConstants.getId);
            idString = idString.replace("\n", "");
            this.id = Integer.valueOf(idString);
            this.isSet = true;
        } catch (Exception e) {
            this.isSet = false;
        }

        return this.id;
    }


    private String jarRunner(String... param) throws IOException, InterruptedException {
        String separator = System.getProperty("file.separator");
        String path = System.getProperty("java.home")
                + separator + "bin" + separator + "java";

        List<String> procParam = new ArrayList<>();
        procParam.add(path);
        procParam.add("-jar");
        procParam.add(jarPath);
        procParam.addAll(Arrays.asList(param));

        ProcessBuilder processBuilder =
                new ProcessBuilder(procParam);

        Process process = processBuilder.start();
        processBuilder.redirectErrorStream(true);
        String result = inputToString(process.getInputStream());
        process.waitFor();
        return result;
    }

    private String inputToString(InputStream inputStream) {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line /*+ System.getProperty("line.separator")*/);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
