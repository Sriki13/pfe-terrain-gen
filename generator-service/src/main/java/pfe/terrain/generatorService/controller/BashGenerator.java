package pfe.terrain.generatorService.controller;

import com.google.gson.Gson;
import pfe.terrain.gen.algo.CommandConstants;
import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.generator.Generator;
import pfe.terrain.generatorService.exception.CannotUseGeneratorException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class BashGenerator implements Generator {

    private String jarPath;
    private int id;
    private boolean isSet = false;
    private String context;
    private boolean contextIsSet;


    public BashGenerator(String jarPath) throws CannotUseGeneratorException {
        this.contextIsSet = false;
        this.jarPath = jarPath;

        this.id = this.getId();

        if (!isSet) {
            throw new CannotUseGeneratorException();
        }
    }

    @Override
    public String generate() {
        try {
            if(contextIsSet){
                return this.runJarWithInput(this.context,CommandConstants.exec,"-i");
            } else {
                return this.runJar(CommandConstants.exec);
            }
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
            String idString = this.runJar(CommandConstants.getId);
            idString = idString.replace("\n", "");
            this.id = Integer.valueOf(idString);
            this.isSet = true;
        } catch (Exception e) {
            this.isSet = false;
        }

        return this.id;
    }

    @Override
    public void setParams(Context map) {
        Map<String,Object> context = new HashMap<>();

        for(Key key : map.getProperties().keySet()){

            try {
                context.put(key.getId(), map.getProperty(key));
            } catch (NoSuchKeyException | KeyTypeMismatch e){
                System.err.println(e.getMessage());
            }
        }

        this.context = new Gson().toJson(context,Map.class);
        this.contextIsSet = true;
    }

    @Override
    public List<Contract> getContracts() {
        return null;
    }


    private String runJar(String... param) throws IOException, InterruptedException {

        Process process = buildProcess(param);
        String result = inputToString(process.getInputStream());
        process.waitFor();
        return result;
    }

    private String runJarWithInput( String input, String... param) throws IOException, InterruptedException{

        Process process = buildProcess(param);
        process.getOutputStream().write(input.getBytes());
        process.getOutputStream().close();
        String result = inputToString(process.getInputStream());
        process.waitFor();
        return result;
    }

    private Process buildProcess(String... param) throws IOException, InterruptedException{
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
        return process;
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
