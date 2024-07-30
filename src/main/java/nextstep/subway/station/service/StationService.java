package nextstep.subway.station.service;

import nextstep.subway.line.entity.Line;
import nextstep.subway.line.service.LineService;
import nextstep.subway.section.entity.Section;
import nextstep.subway.section.entity.Sections;
import nextstep.subway.station.dto.StationPathsResponse;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.station.entity.Station;
import nextstep.subway.station.exception.StationException;
import nextstep.subway.station.exception.StationNotFoundException;
import nextstep.subway.station.repository.StationRepository;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.common.constant.ErrorCode.PATH_DUPLICATE_STATION;
import static nextstep.subway.common.constant.ErrorCode.STATION_NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class StationService {

    private StationRepository stationRepository;
    private LineService lineService;

    public StationService(StationRepository stationRepository, @Lazy LineService lineService) {
        this.stationRepository = stationRepository;
        this.lineService = lineService;
    }

    @Transactional
    public StationResponse saveStation(StationRequest stationRequest) {
        Station station = stationRepository.save(Station.of(stationRequest.getName()));
        return createStationResponse(station);
    }

    public List<StationResponse> findAllStations() {
        return stationRepository.findAll().stream()
                .map(this::createStationResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteStationById(Long id) {
        stationRepository.deleteById(id);
    }

    @Transactional
    public StationPathsResponse retrieveStationPath(Long source, Long target) {
        if (source == target) {
            throw new StationException(String.valueOf(PATH_DUPLICATE_STATION));
        }

        Station sourceStation = getStationByIdOrThrow(source);
        Station targetStation = getStationByIdOrThrow(target);
        List<Line> lineList = lineService.getAllLines();

        List<StationResponse> stationResponseList = new ArrayList<>();
        DijkstraShortestPath shortestPath = setDijkstraGraph(lineList);
        List<Long> shortPaths = shortestPath.getPath(source, target).getVertexList();
        double totalDistance = shortestPath.getPath(source, target).getWeight();

        for(Long stationId : shortPaths) {
            Station station = getStationByIdOrThrow(stationId);
            stationResponseList.add(new StationResponse(station.getId(), station.getName()));
        }

        return new StationPathsResponse(stationResponseList, totalDistance);
    }



    private List<Long> getShortPath(List<Line> lineList, Long source, Long target) {
        return setDijkstraGraph(lineList).getPath(source, target).getVertexList();
    }

    private DijkstraShortestPath setDijkstraGraph(List<Line> lineList) {
        WeightedMultigraph<Long, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);

        for(Line line : lineList) {
            List <Section> sectionList = line.getSections().getSections();
            for(Section section : sectionList) {
                graph.addVertex(section.getUpStation().getId());
                graph.addVertex(section.getDownStation().getId());
                graph.setEdgeWeight(graph.addEdge(section.getUpStation().getId(), section.getDownStation().getId()), section.getDistance());
            }
        }

        return new DijkstraShortestPath(graph);
    }

    private StationResponse createStationResponse(Station station) {
        return new StationResponse(
                station.getId(),
                station.getName()
        );
    }

    public Station getStationByIdOrThrow(Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new StationNotFoundException(String.valueOf(STATION_NOT_FOUND)));
    }

}
