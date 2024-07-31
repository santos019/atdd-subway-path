package nextstep.subway.path.dto;

import nextstep.subway.line.entity.Line;
import nextstep.subway.path.exception.PathException;
import nextstep.subway.section.entity.Section;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

import static nextstep.subway.common.constant.ErrorCode.PATH_NOT_FOUND;

public class GraphModel {
    private WeightedMultigraph<Long, DefaultWeightedEdge> graph;
    private Long source;
    private Long target;

    public GraphModel(Long source, Long target) {
        this.graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        this.source = source;
        this.target = target;
    }

    public Path findPath(List<Line> lineList) {
        createGraphModel(lineList);
        return findShortestPath(source, target);
    }

    public void createGraphModel(List<Line> lineList) {
        for (Line line : lineList) {
            addSectionsToGraph(line);
        }

        containsVertex(source);
        containsVertex(target);
    }

    public Path findShortestPath(Long source, Long target) {
        DijkstraShortestPath<Long, DefaultWeightedEdge> shortestPath =
                new DijkstraShortestPath<>(graph);
        return new Path(shortestPath.getPath(source, target));
    }

    private void addSectionsToGraph(Line line) {
        List<Section> sectionList = line.getSections().getSections();
        for (Section section : sectionList) {
            addEdge(section.getUpStation().getId(), section.getDownStation().getId(), section.getDistance());
        }
    }

    public WeightedMultigraph<Long, DefaultWeightedEdge> getGraph() {
        return graph;
    }

    public void containsVertex(Long vertexId) {
        if (!graph.containsVertex(vertexId)) {
            throw new PathException(String.valueOf(PATH_NOT_FOUND));
        }
    }

    public void addEdge(Long source, Long target, double weight) {
        graph.addVertex(source);
        graph.addVertex(target);
        graph.setEdgeWeight(graph.addEdge(source, target), weight);
    }
}
