package pfe.terrain.gen;

import org.junit.Before;
import org.junit.Test;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.Param;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DiffSolverTest {

    private DiffSolver diffSolver;

    private Contract first;
    private Contract modifiedParamContract;
    private Contract usesCreatedKeyContract;
    private Contract modifiesCreatedKeyContract;

    private Param<Integer> generateParam(String id) {
        return new Param<>(id, Integer.class, "", "", 0, "");
    }

    @Before
    public void setUp() {
        Param<Integer> modified = generateParam("modified");
        Param<Integer> unaffected = generateParam("unaffected");
        Key<Boolean> createdKey = new Key<>("created", Boolean.class);
        first = new TestContract("first", new ArrayList<>(), new ArrayList<>());
        modifiedParamContract = new TestContract("modified", Collections.singletonList(createdKey),
                new ArrayList<>(), Contract.asParamSet(modified));
        usesCreatedKeyContract = new TestContract("usesCreatedKey", new ArrayList<>(),
                Collections.singletonList(createdKey), new ArrayList<>());
        modifiesCreatedKeyContract = new TestContract("modifiesCreated", new ArrayList<>(),
                new ArrayList<>(), Collections.singletonList(createdKey));
        Context original = new Context();
        original.putParam(modified, 5);
        original.putParam(unaffected, 5);
        Context latest = new Context();
        latest.putParam(modified, 10);
        latest.putParam(unaffected, 5);
        diffSolver = new DiffSolver(original, latest);
    }

    @Test
    public void diffTest() {
        List<Contract> got = diffSolver.getContractsToExecute(
                Arrays.asList(first, modifiedParamContract, usesCreatedKeyContract, modifiesCreatedKeyContract));
        assertThat(got.size(), is(3));
        assertThat(got.get(0), is(modifiedParamContract));
        assertThat(got.get(1), is(usesCreatedKeyContract));
        assertThat(got.get(2), is(modifiesCreatedKeyContract));
    }


}
