package pfe.terrain.gen.algo;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ContextTest {

    Param<Integer> height = new Param<>("height",Integer.class,"","",1);
    Param<Integer> water = new Param<>("water",Integer.class,"","",1);
    Param<Integer> sea = new Param<>("sea",Integer.class,"","",1);

    Context context;

    @Before
    public void init(){
        this.context = new Context();
    }

    @Test
    public void mergeTest() throws Exception{
        this.context.putParam(height,18);
        Context toMerge = new Context();
        toMerge.putParam(water,89);

        Context merge = this.context.merge(toMerge);

        assertEquals(18,merge.getParamOrDefault(height).intValue());
        assertEquals(89,merge.getParamOrDefault(water).intValue());
    }

    @Test
    public void mergeWithConflictTest() throws Exception{
        this.context.putParam(height,18);
        this.context.putParam(sea,79);

        Context toMerge = new Context();
        toMerge.putParam(water,89);
        toMerge.putParam(sea,108);

        Context merge = this.context.merge(toMerge);

        assertEquals(18,merge.getParamOrDefault(height).intValue());
        assertEquals(89,merge.getParamOrDefault(water).intValue());
        assertEquals(79,merge.getParamOrDefault(sea).intValue());
    }
}
