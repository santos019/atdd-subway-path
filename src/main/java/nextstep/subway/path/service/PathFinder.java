package nextstep.subway.path.service;

import nextstep.subway.line.entity.Line;
import nextstep.subway.line.service.LineService;
import nextstep.subway.path.dto.GraphModel;
import nextstep.subway.path.dto.Path;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.station.entity.Station;
import nextstep.subway.station.exception.StationException;
import nextstep.subway.station.service.StationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static nextstep.subway.common.constant.ErrorCode.PATH_DUPLICATE_STATION;
import static nextstep.subway.common.constant.ErrorCode.PATH_NOT_FOUND;

@Service
public class PathFinder {

    private StationService stationService;
    private LineService lineService;

    public PathFinder(StationService stationService, LineService lineService) {
        this.stationService = stationService;
        this.lineService = lineService;
    }

    @Transactional(readOnly = true)
    public PathResponse retrieveStationPath(Long source, Long target) {
        validateDistinctSourceAndTarget(source, target);
        validateStationExist(source, target);
        List<Line> lineList = lineService.getAllLines();
        GraphModel graphModel = new GraphModel(source, target);
        Path path = graphModel.findPath(lineList);
        return createPathResponse(path);
    }

    private void validateDistinctSourceAndTarget(Long source, Long target) {
        if (source.equals(target)) {
            throw new StationException(String.valueOf(PATH_DUPLICATE_STATION));
        }
    }

    private void validateStationExist(Long source, Long target) {
        stationService.getStationByIdOrThrow(source);
        stationService.getStationByIdOrThrow(target);
    }

    private PathResponse createPathResponse(Path path) {
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
}
