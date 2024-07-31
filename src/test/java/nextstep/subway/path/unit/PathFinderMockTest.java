package nextstep.subway.path.unit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PathFinderMockTest {


    @DisplayName("출발역과 도착역이 동일하지 않으면 예외가 발생하지 않는다.")
    @Test
    public void validateDistinctSourceAndTarget_noException () {

    }

    @DisplayName("출발역과 도착역이 동일하면 PATH_DUPLICATE_STATION가 발생한다.")
    @Test
    public void validateDistinctSourceAndTarget_Exception () {

    }

    @DisplayName("역이 존재하지 않으면 StationNotFoundException 예외가 발생한다.")
    @Test
    public void validateStationExist_Exception () {

    }

}
