package nextstep.subway;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class StationAcceptanceTest {

    List<String> STATION_NAME_LIST = List.of("강남역", "역삼역");

    public ExtractableResponse<Response> 지하철_노선도_등록(String stationName) {
        Map<String, String> createdStationParams = new HashMap<>();
        createdStationParams.put("name", stationName);

        return RestAssured.given().log().all()
                .body(createdStationParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();

    }

    public List<String> 지하철_노선도_조회() {

        return RestAssured.given().log().all()
                .when().get("/stations")
                .then().log().all()
                .extract().jsonPath().getList("name", String.class);

    }

    public ExtractableResponse<Response> 지하철_노선도_삭제(String stationName) {

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/stations/" + stationName)
                .then().log().all()
                .extract();

    }

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> createdResponse = 지하철_노선도_등록("강남역");
        // then
        assertThat(createdResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> stationNames = 지하철_노선도_조회();
        assertThat(stationNames).containsAnyOf("강남역");
    }

    @DisplayName("2개의 지하철역을 생성한 다음, 2개의 지하철역을 조회한다.")
    @Test
    void showStation() {
        // given
        for (int stationNameIdx = 0; stationNameIdx < STATION_NAME_LIST.size(); stationNameIdx++) {
            String stationName = STATION_NAME_LIST.get(stationNameIdx);
            ExtractableResponse<Response> createdStationResponse = 지하철_노선도_등록(stationName);
            assertThat(createdStationResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        }

        // when
        List<String> stationNames = 지하철_노선도_조회();

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
            ExtractableResponse<Response> createdStationResponse = 지하철_노선도_등록(stationName);
            assertThat(createdStationResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
            createdStationIdList.add(createdStationResponse.body().jsonPath().getLong("id"));

        }

        List<String> stationNamesAfterCreation = 지하철_노선도_조회();

        assertThat(stationNamesAfterCreation.size()).isEqualTo(createdStationNameList.size());
        assertThat(stationNamesAfterCreation.size()).isEqualTo(createdStationIdList.size());
        assertThat(stationNamesAfterCreation).containsAll(createdStationNameList);

        // when
        for (int stationNameIdx = 0; stationNameIdx < createdStationIdList.size(); stationNameIdx++) {
            String stationName = createdStationNameList.get(stationNameIdx);
            ExtractableResponse<Response> deletedStationResponse = 지하철_노선도_삭제(stationName);
            assertThat(deletedStationResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        }

        // then
        List<String> stationNamesAfterDeletion = 지하철_노선도_조회();
        assertThat(stationNamesAfterDeletion).doesNotContainAnyElementsOf(createdStationNameList);

    }

}