package pfe.terrain.factory.storage;

import pfe.terrain.factory.entities.Composition;

import java.util.ArrayList;
import java.util.List;

public class CompoStorage {

    private static List<Composition> compositions = new ArrayList<>();

    public List<Composition> getCompositions(){
        return compositions;
    }

    public void addComposition(Composition composition){
        compositions.add(composition);
    }

    public void clear(){
        compositions = new ArrayList<>();
    }
}
