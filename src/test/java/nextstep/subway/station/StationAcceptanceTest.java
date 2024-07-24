package nextstep.subway.station;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import nextstep.subway.station.dto.StationResponse;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static nextstep.subway.util.StationStep.*;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class StationAcceptanceTest {

    List<String> STATION_NAME_LIST = List.of("강남역", "역삼역");

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        지하철_역_등록("강남역");

        // then
        List<String> stationNames = 지하철_역_전체_조회();
        assertThat(stationNames).containsAnyOf("강남역");
    }

    @DisplayName("2개의 지하철역을 생성한 다음, 2개의 지하철역을 조회한다.")
    @Test
    void showStation() {
        // given
        for (int stationNameIdx = 0; stationNameIdx < STATION_NAME_LIST.size(); stationNameIdx++) {
            String stationName = STATION_NAME_LIST.get(stationNameIdx);
            지하철_역_등록(stationName);
        }

        // when
        List<String> stationNames = 지하철_역_전체_조회();

        // then
        assertThat(stationNames.size()).isEqualTo(STATION_NAME_LIST.size());
        assertThat(stationNames).containsAll(STATION_NAME_LIST);
    }

    @DisplayName("지하철역을 생성한 다음, 해당 지하철역을 삭제한 뒤, 지하철역 목록을 조회하여 삭제된 것을 확인한다.")
    @Test
    void deleteStation() {
        // given
        List<String> createdStationNameList = STATION_NAME_LIST;
        List<Long> createdStationIdList = new ArrayList<>();

        for (int stationNameIdx = 0; stationNameIdx < STATION_NAME_LIST.size(); stationNameIdx++) {
            String stationName = STATION_NAME_LIST.get(stationNameIdx);
            StationResponse createdStationResponse = 지하철_역_등록(stationName);
            createdStationIdList.add(createdStationResponse.getId());
        }

        List<String> stationNamesAfterCreation = 지하철_역_전체_조회();

        assertThat(stationNamesAfterCreation.size()).isEqualTo(createdStationNameList.size());
        assertThat(stationNamesAfterCreation.size()).isEqualTo(createdStationIdList.size());
        assertThat(stationNamesAfterCreation).containsAll(createdStationNameList);

        // when
        for (int stationNameIdx = 0; stationNameIdx < createdStationIdList.size(); stationNameIdx++) {
            지하철_역_삭제(createdStationIdList.get(stationNameIdx));
        }

        // then
        List<String> stationNamesAfterDeletion = 지하철_역_전체_조회();
        assertThat(stationNamesAfterDeletion).doesNotContainAnyElementsOf(createdStationNameList);

    }

}