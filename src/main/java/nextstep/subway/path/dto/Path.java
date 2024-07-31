package nextstep.subway.path.dto;

import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.List;

public class Path {
    private final List<Long> vertexList;
    private final double weight;

    public Path(GraphPath<Long, DefaultWeightedEdge> graphPath) {
        this.vertexList = graphPath.getVertexList();
        this.weight = graphPath.getWeight();
    }

    public List<Long> getVertexList() {
        return vertexList;
    }

    public double getWeight() {
        return weight;
    }
}
