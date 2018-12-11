import graph.TestContract;
import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.DependencySolver;
import pfe.terrain.gen.algo.Generator;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.SerializableKey;
import pfe.terrain.gen.algo.island.geometry.CoordSet;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;
import pfe.terrain.generatorService.controller.ServiceController;
import pfe.terrain.generatorService.holder.Algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

    Contract relaxed  = new TestContract("relaxed",
            asKeySet(new Key<>("POINTS", CoordSet.class)),
            asKeySet(SIZE, SEED));

    Contract mesh = new TestContract("mesh",
            asKeySet(VERTICES, EDGES, FACES),
            asKeySet(new Key<>("POINTS", CoordSet.class), SIZE)
    );

    Contract init = new TestContract("init",
            asKeySet(SEED, SIZE),
            Collections.emptySet());

    Contract noiseshape = new TestContract("noise",
            asKeySet(FACE_WALL_KEY, VERTEX_WALL_KEY),
            asKeySet(FACES, SEED));

    Contract caveWall = new TestContract("wall",
            asKeySet(HEIGHT_KEY),
            asKeySet(FACES, FACE_WALL_KEY, VERTEX_WALL_KEY));

    Contract smallCaveSuppr = new TestContract("suppr",
            asKeySet(),
            asKeySet(FACES, SEED, EDGES, VERTICES),
            asKeySet(FACE_WALL_KEY, VERTEX_WALL_KEY));


    private class TestGenerator implements Generator{
        private List<Contract> contracts;

        public TestGenerator() throws Exception{
            DependencySolver solver = new DependencySolver(Arrays.asList(relaxed,mesh,init,noiseshape,caveWall,smallCaveSuppr));
            this.contracts = solver.orderContracts();
        }

        @Override
        public void generate() {

        }

        @Override
        public Object getProperty(String keyId) {
            return null;
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
    

}
