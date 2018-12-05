package pfe.terrain.gen.algo.name.markov;

import org.junit.Assert;
import org.junit.Test;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.island.IslandMap;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.island.geometry.Face;
import pfe.terrain.gen.algo.island.geometry.FaceSet;
import pfe.terrain.gen.algo.name.Namer;
import pfe.terrain.gen.algo.types.MarkerType;

import java.util.HashSet;

import static org.junit.Assert.assertTrue;
import static pfe.terrain.gen.algo.constraints.Contract.FACES;

public class NameGenTest {

    @Test
    public void nameTest() throws Exception{

        MarkovNameGenerator gen = new MarkovNameGenerator(4,8);

        String name = gen.getName();

        assertTrue(name.length() >= 4);
        assertTrue(name.length() < 8);
    }

    @Test
    public void cityNameTest(){
        Namer namer = new Namer();

        IslandMap map = new IslandMap();

        FaceSet set = new FaceSet();

        for(int i = 0 ; i < 100 ; i++){
            Face face = new Face(new Coord(i,i),new HashSet<>());
            face.putProperty(Namer.CITY_KEY,new MarkerType());
            set.add(face);
        }

        map.putProperty(FACES, set);

        namer.execute(map,new Context());

        for(Face face : set){
            assertTrue(face.hasProperty(Namer.CITY_NAME));
        }

    }
}
