package pfe.terrain.gen;

import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.constraints.context.Context;
import pfe.terrain.gen.algo.constraints.key.Key;
import pfe.terrain.gen.algo.constraints.key.Param;
import pfe.terrain.gen.algo.constraints.key.SerializableKey;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.island.TerrainMap;
import pfe.terrain.gen.algo.island.geometry.Coord;
import pfe.terrain.gen.algo.island.geometry.Edge;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;
import pfe.terrain.gen.algo.types.IntegerType;
import pfe.terrain.gen.algo.types.MarkerType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RiverRidge extends Contract {

    private static final Param<Double> CANYON_TENDENCY_PARAM = Param.generateDefaultDoubleParam("nbRidges",
            "Tendency of rivers to form canyons : 0=no canyons, 1=only canyons", 0.4, "Tendency of forming canyon");

    private static final Param<Double> CANYON_DEPTH_PARAM = Param.generateDefaultDoubleParam("ridgeDepth",
            "How hard the canyons are digging the ground : 0=slowly, 1=very deep", 0.5, "Depth of canyons");

    @Override
    public Set<Param> getRequestedParameters() {
        return asParamSet(CANYON_TENDENCY_PARAM, CANYON_DEPTH_PARAM);
    }


    private static final Key<BooleanType> VERTEX_WATER_KEY =
            new Key<>(VERTICES_PREFIX + "IS_WATER", BooleanType.class);

    private static final Key<DoubleType> VERTEX_HEIGHT_KEY =
            new SerializableKey<>(VERTICES_PREFIX + "HEIGHT", "height", DoubleType.class);

    private static final Key<IntegerType> RIVER_FLOW_KEY =
            new SerializableKey<>(EDGES_PREFIX + "RIVER_FLOW", "riverFlow", IntegerType.class);

    private static final Key<MarkerType> IS_SOURCE_KEY =
            new Key<>(VERTICES_PREFIX + "SOURCE", MarkerType.class);

    private static final Key<MarkerType> IS_RIVER_END_KEY =
            new Key<>(VERTICES_PREFIX + "RIVER_END", MarkerType.class);

    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(VERTICES, EDGES, VERTEX_WATER_KEY, RIVER_FLOW_KEY, IS_SOURCE_KEY, IS_RIVER_END_KEY),
                asKeySet(),
                asKeySet(VERTEX_HEIGHT_KEY)
        );
    }

    @Override
    public void execute(TerrainMap map, Context context) {
        double canyonDepth = context.getParamOrDefault(CANYON_DEPTH_PARAM);
        double canyonTendency = context.getParamOrDefault(CANYON_TENDENCY_PARAM);

        List<Coord> sources = new ArrayList<>();
        for (Coord vertex : map.getProperty(VERTICES)) {
            if (vertex.hasProperty(IS_SOURCE_KEY)) {
                sources.add(vertex);
            }
        }
        sources.sort((o1, o2) -> (int) (o1.x + o1.y - o2.x - o2.y));
        List<Edge> riverEdges = new ArrayList<>();
        for (Edge edge : map.getProperty(EDGES)) {
            if (edge.hasProperty(RIVER_FLOW_KEY)) {
                riverEdges.add(edge);
            }
        }
        List<List<Coord>> flows = new ArrayList<>(sources.size());
        sources.subList(0, (int) (sources.size() * canyonTendency));
        for (Coord source : sources) {
            List<Coord> flow = new ArrayList<>();
            flow.add(source);
            reconstructFlow(flow, riverEdges);
            flows.add(flow);
        }
        for (List<Coord> flow : flows) {
            double multiplier = 1.0;
            double height;
            for (Coord coord : flow) {
                height = coord.getProperty(VERTEX_HEIGHT_KEY).value;
                coord.putProperty(VERTEX_HEIGHT_KEY, new DoubleType(height * multiplier));
                if (multiplier > 0.1) {
                    multiplier -= 0.01 + (0.08 * canyonDepth);
                }
            }
        }
    }

    private void reconstructFlow(List<Coord> flow, List<Edge> edges) throws NoSuchKeyException, KeyTypeMismatch {
        Coord last = flow.get(flow.size() - 1);
        for (Edge edge : edges) {
            if (edge.getStart().equals(last)
                    && edge.getStart().getProperty(VERTEX_HEIGHT_KEY).value >= edge.getEnd().getProperty(VERTEX_HEIGHT_KEY).value
                    && !flow.contains(edge.getEnd())) {
                flow.add(edge.getEnd());
                if (edge.getEnd().hasProperty(IS_RIVER_END_KEY)) {
                    return;
                }
                reconstructFlow(flow, edges);
                break;
            }
            if (edge.getEnd().equals(last)
                    && edge.getEnd().getProperty(VERTEX_HEIGHT_KEY).value >= edge.getStart().getProperty(VERTEX_HEIGHT_KEY).value
                    && !flow.contains(edge.getStart())) {
                flow.add(edge.getStart());
                if (edge.getStart().hasProperty(IS_RIVER_END_KEY)) {
                    return;
                }
                reconstructFlow(flow, edges);
                break;
            }
        }
    }
}
