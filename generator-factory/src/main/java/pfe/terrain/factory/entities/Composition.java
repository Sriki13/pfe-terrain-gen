package pfe.terrain.factory.entities;

import pfe.terrain.factory.exception.ContextParsingException;
import pfe.terrain.factory.parser.ContextParser;
import pfe.terrain.factory.pom.BasePom;
import pfe.terrain.factory.pom.Dependency;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.context.MapContext;
import pfe.terrain.gen.constraints.AdditionalConstraint;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Composition {
    private String name;
    private BasePom pom;
    private Context context;
    private List<Algorithm> algorithms;
    private List<AdditionalConstraint> constraints;

    public Composition(){
        this.name = "default";
        this.context = new MapContext();

        this.pom = new BasePom();
    }

    public Composition(String name, List<Algorithm> algorithms, String context) throws ContextParsingException {
        this();
        this.name = name;
        this.algorithms = algorithms;
        List<Contract> contracts = new ArrayList<>();
        for(Algorithm algo : algorithms){
            this.pom.addDependency(new Dependency(algo.getName()));
            contracts.add(algo.getContract());
        }
        ContextParser parser = new ContextParser(context,contracts);

        this.constraints = parser.getConstraints();
        this.context = parser.getContext();
    }


    public String getName() {
        return name;
    }

    public BasePom getPom() {
        return pom;
    }

    public Context getContext() {
        return context;
    }

    public List<Algorithm> getAlgorithms() {
        return algorithms;
    }

    public List<AdditionalConstraint> getConstraints() {
        return constraints;
    }

    public AdditionalConstraint[] getConstraintsArray() {
        AdditionalConstraint[] constraints = new AdditionalConstraint[this.getConstraints().size()];

        for(int i = 0 ; i < constraints.length ; i++){
            constraints[i] = this.getConstraints().get(i);
        }

        return constraints;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Composition that = (Composition) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
