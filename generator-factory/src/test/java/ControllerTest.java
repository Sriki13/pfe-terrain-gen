import org.junit.Before;
import org.junit.Test;
import pfe.terrain.factory.controller.ServiceController;
import pfe.terrain.factory.entities.Composition;
import pfe.terrain.factory.exception.CannotReachRepoException;
import pfe.terrain.factory.exception.CompositionAlreadyExistException;
import pfe.terrain.factory.exception.NoSuchAlgorithmException;
import pfe.terrain.factory.exception.NoSuchCompoException;
import pfe.terrain.factory.extern.ArtifactoryAlgoLister;
import pfe.terrain.factory.entities.Algorithm;
import pfe.terrain.factory.pom.BasePom;
import pfe.terrain.factory.pom.Dependency;
import pfe.terrain.factory.storage.AlgoStorage;
import pfe.terrain.factory.storage.CompoStorage;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.NotExecutableContract;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.island.geometry.CoordSet;
import pfe.terrain.gen.algo.island.geometry.EdgeSet;
import pfe.terrain.gen.algo.island.geometry.FaceSet;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pfe.terrain.gen.algo.constraints.Contract.asKeySet;

public class ControllerTest {

    private CompoStorage compoStorage;
    private ServiceController controller = new ServiceController(new Lister());

    private class Lister extends AlgoStorage {
        Set<Key> required = asKeySet(
                new Key<>("VERTICES", CoordSet.class),
                new Key<>("EDGES", EdgeSet.class),
                new Key<>("FACES", FaceSet.class));
        @Override
        public List<Algorithm> getAlgoList() throws Exception {
            return Arrays.asList(
                    new Algorithm("salut",
                            new NotExecutableContract("salut",new HashSet<>(),new Constraints(new HashSet<>(),required))),
                    new Algorithm("test"));
        }

        @Override
        public List<Algorithm> algosFromStrings(List<String> algoIds) throws Exception {
            return super.algosFromStrings(algoIds);
        }
    }

    private class FailLister extends AlgoStorage{

        @Override
        public List<Algorithm> getAlgoList() throws Exception {
            throw new CannotReachRepoException();
        }
    }

    @Before
    public void init(){
        this.controller = new ServiceController(new Lister());
        this.compoStorage = new CompoStorage();
        this.compoStorage.clear();
    }

    @Test
    public void pomTest() throws Exception{
        BasePom pom = this.controller.getGenerator(Arrays.asList("salut"));

        assertTrue(pom.contain(new Dependency("salut")));
    }

    @Test (expected = NoSuchAlgorithmException.class)
    public void missingAlgoForGenTest() throws Exception{
        this.controller.getGenerator(Arrays.asList("azeazea"));
    }

    @Test
    public void getAlgoListTest() throws Exception{
        List<Algorithm> algos = this.controller.getAlgoList();

        assertTrue(algos.contains(new Algorithm("salut")));
        assertTrue(algos.contains(new Algorithm("test")));
    }

    @Test (expected = CannotReachRepoException.class)
    public void failListTest() throws Exception{
        new ServiceController(new FailLister()).getAlgoList();
    }

    @Test
    public void getCompoTest(){
        assertEquals(0,this.controller.getCompositions().size());

        Composition compo = new Composition();

        this.compoStorage.addComposition(compo);

        assertEquals(1,this.controller.getCompositions().size());

        assertEquals(compo,this.controller.getCompositions().get(0));
    }

    @Test
    public void addCompoTest() throws Exception{
        Composition compo = this.controller.addComposition("test",Arrays.asList("salut"),"context");

        assertEquals(1,this.compoStorage.getCompositions().size());

        assertEquals(compo,this.compoStorage.getCompositions().get(0));

        assertTrue(this.controller.getCompositions().contains(compo));
    }

    @Test(expected = NoSuchAlgorithmException.class)
    public void missingAlgoTest() throws Exception{
        this.controller.addComposition("test",Arrays.asList("qsdsqd"),"context");
    }

    @Test(expected = CompositionAlreadyExistException.class)
    public void alreadyExistingCompoException() throws Exception{
        this.controller.addComposition("test",Arrays.asList("salut"),"context");

        this.controller.addComposition("test",Arrays.asList("salut"),"context");

    }

    @Test
    public void getPomTest() throws Exception{
        Composition compo =new Composition("test",Arrays.asList(new Algorithm("salut")),"context");
        this.compoStorage.addComposition(compo);
        BasePom pom = this.controller.getCompositionPom(compo.getName());

        assertEquals(compo.getPom(),pom);
    }

    @Test
    public void getContextTest() throws Exception{
        Composition compo =new Composition("test",Arrays.asList(new Algorithm("salut")),"context");
        this.compoStorage.addComposition(compo);
        String context = this.controller.getCompositionContext(compo.getName());

        assertEquals(compo.getContext(),context);
    }

    @Test(expected = NoSuchCompoException.class)
    public void noCompoTest() throws Exception{
        this.controller.getCompositionContext("salut");
    }

    @Test
    public void removeTest() throws Exception{

        Composition composition = new Composition("test",new ArrayList<>(),"{}");
        this.compoStorage.addComposition(composition);
        assertEquals(1,this.compoStorage.getCompositions().size());

        this.controller.deleteComposition(composition.getName());

        assertEquals(0,this.compoStorage.getCompositions().size());


    }

    @Test (expected = NoSuchCompoException.class)
    public void deleteNonExistingCompo() throws Exception{
        this.controller.deleteComposition("wowowowo");
    }
}
