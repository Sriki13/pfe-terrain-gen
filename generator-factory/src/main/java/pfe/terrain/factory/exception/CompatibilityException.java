package pfe.terrain.factory.exception;

import pfe.terrain.factory.entities.Algorithm;

public class CompatibilityException extends Exception{

    public CompatibilityException(Algorithm a, Algorithm b){
        super(a.getId() + " and " + b.getId() + " are not compatible");
    }
}
