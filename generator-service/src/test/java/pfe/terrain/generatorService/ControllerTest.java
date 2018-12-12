package pfe.terrain.generatorService;

import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.DependencySolver;
import pfe.terrain.gen.algo.Generator;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.constraints.key.SerializableKey;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.geometry.CoordSet;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;
import pfe.terrain.generatorService.controller.ServiceController;
import pfe.terrain.generatorService.holder.Algorithm;
import pfe.terrain.generatorService.holder.Parameter;
import pfe.terrain.generatorService.parser.JsonParser;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static pfe.terrain.gen.algo.constraints.Contract.*;

public class ControllerTest{
    private ServiceController controller;
    private Generator generator;

    static final Key<BooleanType> FACE_WALL_KEY =
            new SerializableKey<>(FACES_PREFIX + "IS_WALL", "isWall", BooleanType.class);

    static final Key<BooleanType> VERTEX_WALL_KEY =
            new SerializableKey<>(VERTICES_PREFIX + "IS_WALL", "isWall", BooleanType.class);

    static final Key<DoubleType> HEIGHT_KEY =
            new SerializableKey<>(VERTICES_PREFIX + "HEIGHT", "height", DoubleType.class);

    Param<Boolean> wow = new Param<Boolean>("wow",Boolean.class,"wow","wow",false,"wow");
    Param<Boolean> rrr = new Param<Boolean>("rrr",Boolean.class,"rrr","rrr",false,"rrr");
    Param<Boolean> test = new Param<Boolean>("test",Boolean.class,"test","test",false,"test");

    Contract relaxed  = new TestContract("relaxed",
            asKeySet(new Key<>("POINTS", CoordSet.class)),
            asKeySet(SIZE, SEED),
            Arrays.asList(rrr));

    Contract mesh = new TestContract("mesh",
            asKeySet(VERTICES, EDGES, FACES),
            asKeySet(new Key<>("POINTS", CoordSet.class), SIZE),
            Arrays.asList(test));

    Contract init = new TestContract("init",
            asKeySet(SEED, SIZE),
            Collections.emptySet(),
            Arrays.asList(wow));

    Contract noiseshape = new TestContract("noise",
            asKeySet(FACE_WALL_KEY, VERTEX_WALL_KEY),
            asKeySet(FACES, SEED),
            new ArrayList<>());

    Contract caveWall = new TestContract("wall",
            asKeySet(HEIGHT_KEY),
            asKeySet(FACES, FACE_WALL_KEY, VERTEX_WALL_KEY),
            new ArrayList<>());

    Contract smallCaveSuppr = new TestContract("suppr",
            asKeySet(),
            asKeySet(FACES, SEED, EDGES, VERTICES),
            asKeySet(FACE_WALL_KEY, VERTEX_WALL_KEY),new ArrayList<>());


    private class TestGenerator implements Generator{
        private List<Contract> contracts;
        private TerrainMap map;

        public TestGenerator() throws Exception{
            DependencySolver solver = new DependencySolver(Arrays.asList(relaxed,mesh,init,noiseshape,caveWall,smallCaveSuppr));

            this.contracts = solver.orderContracts();
            this.map = new TerrainMap();
        }

        @Override
        public void generate() {
            this.map.putProperty(VERTEX_WALL_KEY,new BooleanType(false));
        }

        @Override
        public Object getProperty(String keyId) {
            for (Map.Entry<Key<?>, Object> entry : map.getProperties().entrySet()) {
                if (entry.getKey().getId().equals(keyId)) {
                    return entry.getValue();
                }
            }
            throw new NoSuchKeyException(keyId);
        }

        @Override
        public void setParams(Context map) {

        }

        @Override
        public List<Contract> getContracts() {
            return this.contracts;
        }

        @Override
        public byte[] getExecutionChart() {
            return new byte[0];
        }
    }

    @Before
    public void init() throws Exception{
        this.generator = new TestGenerator();
        this.controller = new ServiceController(this.generator);
    }

    @Test
    public void getAlgoListTest(){
        List<Algorithm> algorithms = this.controller.getAlgoList();
        for(Algorithm algo : algorithms){
            assertEquals(algo.getName(),this.generator.getContracts().get(algo.getPos()).getName());
        }
    }

    @Test
    public void executeTest(){
        this.controller.execute();
        assertEquals(false,((BooleanType)this.controller.getProperty(VERTEX_WALL_KEY.getId())).value);
    }

    @Test(expected = NoSuchKeyException.class)
    public void noSuchKeyTest(){
        this.controller.getProperty("azeazeaze");
    }

    @Test
    public void paramTest(){
        List<Parameter> parameters = this.controller.getParameters();
        assertEquals(3,parameters.size());
    }

    @Test
    public void contextTest(){
        this.controller.setContext("{\"rrr\" : false, \"test\" : false,\"azeaze\" : \"ok\"}");

        assertEquals(false,this.controller.getContext().getParamOrDefault(rrr).booleanValue());
        assertEquals(false,this.controller.getContext().getParamOrDefault(test).booleanValue());

        assertEquals(2,this.controller.getContext().getProperties().size());
    }
    

}
