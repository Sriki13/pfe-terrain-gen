package pfe.terrain.gen.parser;

import com.google.gson.Gson;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.context.MapContext;
import pfe.terrain.gen.constraints.AdditionalConstraint;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ParamParser {
    private String contextPath;

    private String contextString;

    private String contextKey = "context";
    private String constraintKey = "constraint";

    public ParamParser(String path) {
        this.contextPath = path;
    }

    public ParamParser() {
        this.contextPath = this.getClass().getClassLoader().getResource("context.json").getFile();
    }

    public String getContextString() {
        try {
            FileReader reader = new FileReader(this.contextPath);

            Scanner scanner = new Scanner(reader);
            StringBuilder builder = new StringBuilder();

            while (scanner.hasNextLine()) {
                builder.append(scanner.nextLine());
            }

            return builder.toString();

        } catch (Exception e) {
            System.err.println("could not load context file, loading empty context");
            return "{}";
        }
    }

    public Context getContext(List<Contract> contracts) {
        this.readContext();

        Map<String, Object> init = new Gson().fromJson(this.contextString, Map.class);
        if (init.containsKey(this.contextKey)) {

            return new MapContext((Map) init.get(this.contextKey), contracts);
        }

        return new MapContext();
    }

    public List<AdditionalConstraint> getConstraints(List<Contract> contracts) {
        this.readContext();

        String stringContext = this.getContextString();
        Map<String, Object> init = new Gson().fromJson(stringContext, Map.class);
        if (init.containsKey(this.constraintKey)) {
            List<Map> constraints = (List<Map>) init.get(this.constraintKey);

            ConstraintParser parser = new ConstraintParser();
            return parser.listToConstraints(constraints, contracts);
        }
        return new ArrayList<>();
    }

    private void readContext() {
        if (this.contextString == null) {
            this.contextString = this.getContextString();
        }
    }
}
