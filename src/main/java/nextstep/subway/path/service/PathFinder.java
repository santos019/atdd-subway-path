package nextstep.subway.path.service;

import nextstep.subway.line.entity.Line;
import nextstep.subway.line.service.LineService;
import nextstep.subway.path.dto.GraphModel;
import nextstep.subway.path.dto.Path;
import nextstep.subway.section.entity.Section;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.station.entity.Station;
import nextstep.subway.station.exception.StationException;
import nextstep.subway.station.service.StationService;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static nextstep.subway.common.constant.ErrorCode.*;

@Service
public class PathFinder {

    private StationService stationService;
    private LineService lineService;

    public PathFinder(StationService stationService, LineService lineService) {
        this.stationService = stationService;
        this.lineService = lineService;
    }

    @Transactional
    public PathResponse retrieveStationPath(Long source, Long target) {
        if (source.equals(target)) {
            throw new StationException(String.valueOf(PATH_DUPLICATE_STATION));
        }

        stationService.getStationByIdOrThrow(source);
        stationService.getStationByIdOrThrow(target);
        List<Line> lineList = lineService.getAllLines();

        GraphModel graphModel = createGraphModel(lineList, source, target);

        DijkstraShortestPath<Long, DefaultWeightedEdge> shortestPath =
                new DijkstraShortestPath<>(graphModel.getGraph());
        Path path = new Path(shortestPath.getPath(source, target));

        if (path.getVertexList() == null || path.getVertexList().isEmpty()) {
            throw new StationException(String.valueOf(PATH_NOT_FOUND));
        }

        List<StationResponse> stationResponseList = new ArrayList<>();
        for (Long stationId : path.getVertexList()) {
            Station station = stationService.getStationByIdOrThrow(stationId);
            stationResponseList.add(new StationResponse(station.getId(), station.getName()));
        }

        return new PathResponse(stationResponseList, path.getWeight());
    }

    private void validateHasSourceAndTarget(GraphModel graph, Long source, Long target) {
        graph.containsVertex(source);
        graph.containsVertex(target);
    }

    private GraphModel createGraphModel(List<Line> lineList, Long source, Long target) {
        WeightedMultigraph<Long, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        GraphModel graphModel = new GraphModel(graph);

        for (Line line : lineList) {
            List<Section> sectionList = line.getSections().getSections();
            for (Section section : sectionList) {
                graphModel.addEdge(section.getUpStation().getId(), section.getDownStation().getId(), section.getDistance());
            }
        }

        validateHasSourceAndTarget(graphModel, source, target);

        return graphModel;
    }
}
