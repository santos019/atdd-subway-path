package nextstep.subway.path.service;

import nextstep.subway.line.entity.Line;
import nextstep.subway.line.service.LineService;
import nextstep.subway.section.entity.Section;
import nextstep.subway.station.dto.StationPathsResponse;
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
    public StationPathsResponse retrieveStationPath(Long source, Long target) {
        if (source == target) {
            throw new StationException(String.valueOf(PATH_DUPLICATE_STATION));
        }

        Station sourceStation = stationService.getStationByIdOrThrow(source);
        Station targetStation = stationService.getStationByIdOrThrow(target);
        List<Line> lineList = lineService.getAllLines();

        List<StationResponse> stationResponseList = new ArrayList<>();
        WeightedMultigraph weightedMultigraph = setDijkstraGraph(lineList);
        validateHasSourceAndTarget(weightedMultigraph, source, target);
        DijkstraShortestPath<Long, DefaultWeightedEdge> shortestPath = new DijkstraShortestPath(weightedMultigraph);

        GraphPath<Long, DefaultWeightedEdge> path = shortestPath.getPath(source, target);

        if (path == null) {
            throw new StationException(String.valueOf(PATH_NOT_FOUND));
        }
        List<Long> shortPaths = path.getVertexList();
        double totalDistance = path.getWeight();

        for (Long stationId : shortPaths) {
            Station station = stationService.getStationByIdOrThrow(stationId);
            stationResponseList.add(new StationResponse(station.getId(), station.getName()));
        }

        return new StationPathsResponse(stationResponseList, totalDistance);
    }

    private void validateHasSourceAndTarget(WeightedMultigraph<Long, DefaultWeightedEdge> graph, Long source, Long target) {
        if (!graph.containsVertex(source)) {
            throw new StationException(String.valueOf(PATH_NOT_FOUND_SOURCE_STATION));
        }

        if (!graph.containsVertex(target)) {
            throw new StationException(String.valueOf(PATH_NOT_FOUND_TARGET_STATION));
        }
    }

    private WeightedMultigraph setDijkstraGraph(List<Line> lineList) {
        WeightedMultigraph<Long, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);

        for (Line line : lineList) {
            List<Section> sectionList = line.getSections().getSections();
            for (Section section : sectionList) {
                graph.addVertex(section.getUpStation().getId());
                graph.addVertex(section.getDownStation().getId());
                graph.setEdgeWeight(graph.addEdge(section.getUpStation().getId(), section.getDownStation().getId()), section.getDistance());
            }
        }

        return graph;
    }
}
