package nextstep.subway.path.dto;

import nextstep.subway.line.entity.Line;
import nextstep.subway.path.exception.PathException;
import nextstep.subway.section.entity.Section;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.station.entity.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.ArrayList;
import java.util.List;

import static nextstep.subway.common.constant.ErrorCode.PATH_NOT_FOUND;

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

    public PathResponse createPathResponse(Path path, List<Line> lineList) {
        if (path.getVertexList() == null || path.getVertexList().isEmpty()) {
            throw new PathException(String.valueOf(PATH_NOT_FOUND));
        }

        List<StationResponse> stationResponseList = new ArrayList<>();
        for (Long stationId : path.getVertexList()) {
            Station station = getStation(lineList, stationId);
            stationResponseList.add(new StationResponse(station.getId(), station.getName()));
        }

        return new PathResponse(stationResponseList, path.getWeight());
    }

    public Station getStation(List<Line> lineList, Long stationId) {
        for (Line line : lineList) {
            Station foundStation = findStationInLine(line, stationId);
            if (foundStation != null) {
                return foundStation;
            }
        }
        throw new PathException(String.valueOf(PATH_NOT_FOUND));
    }

    private Station findStationInLine(Line line, Long stationId) {
        List<Section> sectionList = line.getSections().getSections();
        for (Section section : sectionList) {
            if (section.getUpStation().getId().equals(stationId)) {
                return section.getUpStation();
            }
            if (section.getDownStation().getId().equals(stationId)) {
                return section.getDownStation();
            }
        }
        throw new PathException(String.valueOf(PATH_NOT_FOUND));
    }
}
