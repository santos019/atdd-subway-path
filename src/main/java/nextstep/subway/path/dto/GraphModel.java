package nextstep.subway.path.dto;

import nextstep.subway.line.entity.Line;
import nextstep.subway.path.exception.PathException;
import nextstep.subway.section.entity.Section;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

import static nextstep.subway.common.constant.ErrorCode.PATH_DUPLICATE_STATION;
import static nextstep.subway.common.constant.ErrorCode.PATH_NOT_FOUND;

public class GraphModel {
    private WeightedMultigraph<Long, DefaultWeightedEdge> graph;
    private Long source;
    private Long target;

    public GraphModel(Long source, Long target) {
        validateDuplicate(source, target);
        this.graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        this.source = source;
        this.target = target;
    }

    public Path findPath(List<Line> lineList) {
        createGraphModel(lineList);
        return findShortestPath();
    }

    public void createGraphModel(List<Line> lineList) {
        if(lineList.isEmpty()) {
            throw new PathException(String.valueOf(PATH_NOT_FOUND));
        }

        for (Line line : lineList) {
            addSectionsToGraph(line);
        }

        containsVertex(source);
        containsVertex(target);
    }

    public Path findShortestPath() {
        validateDuplicate(source, target);
        DijkstraShortestPath<Long, DefaultWeightedEdge> shortestPath =
                new DijkstraShortestPath<>(graph);
        return new Path(shortestPath.getPath(source, target));
    }

    public void addSectionsToGraph(Line line) {
        List<Section> sectionList = line.getSections().getSections();

        if (sectionList.isEmpty()) {
            throw new PathException(String.valueOf(PATH_NOT_FOUND));
        }
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

    public void addEdge(Long newSource, Long newTarget, double weight) {
        validateDuplicate(newSource, newTarget);
        graph.addVertex(newSource);
        graph.addVertex(newTarget);
        graph.setEdgeWeight(graph.addEdge(newSource, newTarget), weight);
    }

    public void validateDuplicate (Long source, Long target) {
        if(source.equals(target)) {
            throw new PathException(String.valueOf(PATH_DUPLICATE_STATION));
        }
    }
}
