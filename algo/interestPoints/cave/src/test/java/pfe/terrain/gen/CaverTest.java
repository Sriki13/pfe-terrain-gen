package pfe.terrain.gen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pfe.terrain.gen.Caver.NB_CAVE;
import static pfe.terrain.gen.algo.constraints.Contract.FACES;
import static pfe.terrain.gen.algo.constraints.Contract.SEED;

import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.island.Biome;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.island.geometry.Face;
import pfe.terrain.gen.algo.island.geometry.FaceSet;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.IntegerType;

import java.util.HashSet;


public class CaverTest {
    private TerrainMap map;
    private Context context;

    @Before
    public void init(){
        this.context = new Context();
        this.map = new TerrainMap();
        this.map.putProperty(SEED,10);

        FaceSet set = new FaceSet();

        for(int i = 0; i< 10 ; i++){
            for (int j = 0; j < 10; j++){
                Face face = new Face(new Coord(i,j),new HashSet<>());
                face.putProperty(Caver.FACE_WATER_KEY,new BooleanType(false));
                face.putProperty(Caver.FACE_BIOME_KEY, Biome.ALPINE);

                set.add(face);
            }
        }

        this.map.putProperty(FACES,set);

    }

    @Test
    public void generateCaveTest() {
        Caver caver = new Caver();

        caver.execute(this.map,this.context);
        int nbCave = 0;

        for(Face face : this.map.getProperty(FACES)){
            if(face.hasProperty(Caver.FACE_CAVE_KEY)){
                nbCave++;
            }
        }

        assertEquals(NB_CAVE.getDefaultValue().intValue(),nbCave);
    }

    @Test
    public void generateCaveWithContextTest() {
        Caver caver = new Caver();
        this.context.putParam(NB_CAVE,18);
        caver.execute(this.map,this.context);
        int nbCave = 0;

        for(Face face : this.map.getProperty(FACES)){
            if(face.hasProperty(Caver.FACE_CAVE_KEY)){
                nbCave++;
            }
        }

        assertEquals(18,nbCave);
    }
}
