package nextstep.subway.station.dto;

import java.util.ArrayList;
import java.util.List;

public class StationPathsResponse {

    private List<StationResponse> stationResponseList = new ArrayList<>();
    private Double distance;

    public StationPathsResponse() {}

    public StationPathsResponse(List<StationResponse> stationResponseList, Double distance) {
        this.stationResponseList = stationResponseList;
        this.distance = distance;
    }

    public StationPathsResponse of (List<StationResponse> stationResponseList, Double distance) {
        return new StationPathsResponse(stationResponseList, distance);
    }

    public List<StationResponse> getStationResponseList() {
        return stationResponseList;
    }

    public Double getDistance() {
        return distance;
    }
}
