package pfe.terrain.factory;

import org.junit.Before;
import org.junit.Test;
import pfe.terrain.factory.compatibility.SimpleCompatibility;
import pfe.terrain.factory.controller.ServiceController;
import pfe.terrain.factory.entities.Composition;
import pfe.terrain.factory.exception.*;
import pfe.terrain.factory.entities.Algorithm;
import pfe.terrain.factory.pom.BasePom;
import pfe.terrain.factory.pom.Dependency;
import pfe.terrain.factory.storage.AlgoStorage;
import pfe.terrain.factory.storage.CompatibilityStorage;
import pfe.terrain.factory.storage.CompoStorage;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.NotExecutableContract;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.island.geometry.CoordSet;
import pfe.terrain.gen.algo.island.geometry.EdgeSet;
import pfe.terrain.gen.algo.island.geometry.FaceSet;
import pfe.terrain.gen.exception.MissingRequiredException;
import pfe.terrain.gen.exception.UnsolvableException;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pfe.terrain.gen.algo.constraints.Contract.asKeySet;

public class ControllerTest {

    private CompoStorage compoStorage;
    private ServiceController controller = new ServiceController(new Lister());
    private CompatibilityStorage compatibilityStorage;

    private class Lister extends AlgoStorage {
        Set<Key> created = asKeySet(
                new Key<>("VERTICES", CoordSet.class),
                new Key<>("EDGES", EdgeSet.class),
                new Key<>("FACES", FaceSet.class));
        @Override
        public List<Algorithm> getAlgoList() throws Exception {
            return Arrays.asList(
                    new Algorithm(
                            new NotExecutableContract("salut","test contract",new HashSet<>(),new Constraints(new HashSet<>(), created)),"test"),
                    new Algorithm(
                            new NotExecutableContract("test","test contract",new HashSet<>(),new Constraints(
                                    asKeySet(new Key<>("EDGES", EdgeSet.class)),
                                    asKeySet(new Key<>("VERTICES", CoordSet.class)))),"test"),
                    new Algorithm(
                            new NotExecutableContract("wow","test contract",new HashSet<>(),new Constraints(
                                    asKeySet(new Key<>("VERTICES", CoordSet.class)),
                                    asKeySet(new Key<>("EDGES", EdgeSet.class)))),"test"),
                    new Algorithm(
                            new NotExecutableContract("face","test contract",new HashSet<>(),new Constraints(
                                    asKeySet(new Key<>("VERTICES", CoordSet.class)),
                                    asKeySet(new Key<>("FACES", FaceSet.class)))),"test"),
                    new Algorithm(new NotExecutableContract("first","test contract",new HashSet<>(),new Constraints(new HashSet<>(), created)),"test"),
                    new Algorithm("second"),
                    new Algorithm(
                            new NotExecutableContract("all","test contract",new HashSet<>(),new Constraints(
                                    asKeySet(),
                                    asKeySet(new Key<>("FACES", FaceSet.class),new Key<>("EDGES",EdgeSet.class)))),"test"));
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
        this.compatibilityStorage = new CompatibilityStorage();
        this.controller = new ServiceController(new Lister());
        this.compoStorage = new CompoStorage();
        this.compoStorage.clear();
    }

    @Test
    public void pomTest() throws Exception{
        BasePom pom = this.controller.getGenerator(Arrays.asList("salut"));

        assertTrue(pom.contain(new Dependency("test")));
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
        Composition compo = this.controller.addComposition("test",Arrays.asList("salut"),"{}");

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
        this.controller.addComposition("test",Arrays.asList("salut"),"{}");

        this.controller.addComposition("test",Arrays.asList("salut"),"{}");

    }

    @Test
    public void getPomTest() throws Exception{
        Composition compo =new Composition("test",Arrays.asList(new Algorithm("salut")),"{}");
        this.compoStorage.addComposition(compo);
        BasePom pom = this.controller.getCompositionPom(compo.getName());

        assertEquals(compo.getPom(),pom);
    }

    @Test
    public void getContextTest() throws Exception{
        Composition compo =new Composition("test",Arrays.asList(new Algorithm("salut")),"{}");
        this.compoStorage.addComposition(compo);

        assertTrue(compo.getContext().getProperties().isEmpty());
    }

    @Test(expected = NoSuchCompoException.class)
    public void noCompoTest() throws Exception{
        this.controller.getCompositionContext("{}");
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

    @Test (expected = MissingRequiredException.class)
    public void MissingRequiredException() throws Exception{
        this.controller.addComposition("test",Arrays.asList("test"),"{}");
    }

    @Test (expected = UnsolvableException.class)
    public void cannotComposeException() throws Exception{
        this.controller.addComposition("test",Arrays.asList("wow","face","test"),"{}");
    }

    @Test (expected = UnsolvableException.class)
    public void constraintsHinderResolution() throws Exception{
        this.controller.addComposition("test",Arrays.asList("first","second"),"{\"constraint\": [\n" +
                "      {\n" +
                "        \"name\": \"order\",\n" +
                "        \"before\": \"first\",\n" +
                "        \"after\": \"second\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"order\",\n" +
                "        \"before\": \"second\",\n" +
                "        \"after\": \"first\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }");
    }

    @Test(expected = CompatibilityException.class)
    public void failCompatibilityChechTest() throws Exception{
        AlgoStorage storage = new Lister();
        List<Algorithm> algos = storage.algosFromStrings(Arrays.asList("salut","test"));
        compatibilityStorage.putCompatibility(algos.get(0),algos.get(1), SimpleCompatibility.UNCOMPATIBLE);
        Composition compo = this.controller.addComposition("test",Arrays.asList("salut","test"),"{}");
    }

    @Test
    public void middleCompatibilityException() throws Exception{
        AlgoStorage storage = new Lister();
        List<Algorithm> algos = storage.algosFromStrings(Arrays.asList("all","test"));
        compatibilityStorage.putCompatibility(algos.get(0),algos.get(1), SimpleCompatibility.COMPATIBLE_BEWARE);
        Composition compo = this.controller.addComposition("test",Arrays.asList("test","all"),"{}");
    }

    @Test
    public void addingCompatibililtyTest() throws Exception{
        AlgoStorage storage = new Lister();

        this.controller.addCompatibility(Arrays.asList("all","test","salut"),SimpleCompatibility.COMPATIBLE_BEWARE.getId());
        Algorithm all = storage.algosFromStrings(Arrays.asList("all")).get(0);
        Algorithm test = storage.algosFromStrings(Arrays.asList("test")).get(0);
        Algorithm salut = storage.algosFromStrings(Arrays.asList("salut")).get(0);

        assertEquals(this.compatibilityStorage.getCompatibility(all,test),SimpleCompatibility.COMPATIBLE_BEWARE);
        assertEquals(this.compatibilityStorage.getCompatibility(all,salut),SimpleCompatibility.COMPATIBLE_BEWARE);
        assertEquals(this.compatibilityStorage.getCompatibility(test,salut),SimpleCompatibility.COMPATIBLE_BEWARE);
    }

    @Test (expected = NoSuchAlgorithmException.class)
    public void noSuchAlgoCompatibility() throws Exception{
        this.controller.addCompatibility(Arrays.asList("azeaze","azeazrr"),SimpleCompatibility.COMPATIBLE_BEWARE.getId());
    }
}
