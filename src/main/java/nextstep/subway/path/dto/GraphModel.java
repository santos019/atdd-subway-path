package nextstep.subway.path.dto;

import nextstep.subway.station.exception.StationException;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import static nextstep.subway.common.constant.ErrorCode.PATH_NOT_FOUND;

public class GraphModel {
    private final WeightedMultigraph<Long, DefaultWeightedEdge> graph;

    public GraphModel(WeightedMultigraph<Long, DefaultWeightedEdge> graph) {
        this.graph = graph;
    }

    public WeightedMultigraph<Long, DefaultWeightedEdge> getGraph() {
        return graph;
    }

    public void containsVertex(Long vertexId) {
        if(!graph.containsVertex(vertexId)) {
            throw new StationException(String.valueOf(PATH_NOT_FOUND));
        }
    }

    public void addEdge(Long source, Long target, double weight) {
        graph.addVertex(source);
        graph.addVertex(target);
        graph.setEdgeWeight(graph.addEdge(source, target), weight);
    }
}
