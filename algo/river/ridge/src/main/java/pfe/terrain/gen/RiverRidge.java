package pfe.terrain.gen;

import pfe.terrain.gen.algo.Context;
import pfe.terrain.gen.algo.IslandMap;
import pfe.terrain.gen.algo.Key;
import pfe.terrain.gen.algo.SerializableKey;
import pfe.terrain.gen.algo.constraints.Constraints;
import pfe.terrain.gen.algo.constraints.Contract;
import pfe.terrain.gen.algo.exception.KeyTypeMismatch;
import pfe.terrain.gen.algo.exception.NoSuchKeyException;
import pfe.terrain.gen.algo.geometry.Coord;
import pfe.terrain.gen.algo.geometry.Edge;
import pfe.terrain.gen.algo.types.BooleanType;
import pfe.terrain.gen.algo.types.DoubleType;
import pfe.terrain.gen.algo.types.IntegerType;

import java.util.ArrayList;
import java.util.List;

public class RiverRidge extends Contract {

    public static final Key<BooleanType> vertexWaterKey =
            new Key<>(verticesPrefix + "IS_WATER", BooleanType.class);
    public static final Key<DoubleType> vertexHeightKey =
            new SerializableKey<>(verticesPrefix + "HEIGHT", "height", DoubleType.class);


    public static final Key<IntegerType> riverFlowKey =
            new SerializableKey<>(edgesPrefix + "RIVER_FLOW", "riverFlow", IntegerType.class);
    public static final Key<Boolean> isSourceKey =
            new Key<>(verticesPrefix + "SOURCE", Boolean.class);
    public static final Key<Boolean> isRiverEndKey =
            new Key<>(verticesPrefix + "RIVER_END", Boolean.class);

    public static final Key<Boolean> keka =
            new Key<>("keka", Boolean.class);

    @Override
    public Constraints getContract() {
        return new Constraints(
                asKeySet(vertices, edges, vertexWaterKey, riverFlowKey, isSourceKey, isRiverEndKey),
                asKeySet(keka),
                asKeySet(vertexHeightKey)
        );
    }

    @Override
    public void execute(IslandMap map, Context context) {
        List<Coord> sources = new ArrayList<>();
        for (Coord vertex : map.getVertices()) {
            if (vertex.getProperty(isSourceKey)) {
                sources.add(vertex);
            }
        }
        sources.sort((o1, o2) -> (int) (o1.x + o1.y - o2.x - o2.y));
        List<Edge> riverEdges = new ArrayList<>();
        for (Edge edge : map.getEdges()) {
            if (edge.getProperty(riverFlowKey).value == 1) {
                riverEdges.add(edge);
            }
        }
        List<List<Coord>> flows = new ArrayList<>(sources.size());
        for (Coord source : sources) {
            List<Coord> flow = new ArrayList<>();
            flow.add(source);
            reconstructFlow(flow, riverEdges);
            flows.add(flow);
        }
        for (List<Coord> flow : flows) {
            double multiplicator = 1.0;
            double height;
            for (Coord coord : flow) {
                height = coord.getProperty(vertexHeightKey).value;
                coord.putProperty(vertexHeightKey, new DoubleType(height * multiplicator));
                multiplicator *= 0.9;
            }
        }
    }

    private void reconstructFlow(List<Coord> flow, List<Edge> edges) throws NoSuchKeyException, KeyTypeMismatch {
        Coord last = flow.get(flow.size() - 1);
        for (Edge edge : edges) {
            if (edge.getStart().equals(last)
                    && edge.getStart().getProperty(vertexHeightKey).value >= edge.getEnd().getProperty(vertexHeightKey).value
                    && !flow.contains(edge.getEnd())) {
                flow.add(edge.getEnd());
                if (edge.getEnd().getProperty(isRiverEndKey)) {
                    return;
                }
                reconstructFlow(flow, edges);
                break;
            }
            if (edge.getEnd().equals(last)
                    && edge.getEnd().getProperty(vertexHeightKey).value >= edge.getStart().getProperty(vertexHeightKey).value
                    && !flow.contains(edge.getStart())) {
                flow.add(edge.getStart());
                if (edge.getStart().getProperty(isRiverEndKey)) {
                    return;
                }
                reconstructFlow(flow, edges);
                break;
            }
        }
    }
}
