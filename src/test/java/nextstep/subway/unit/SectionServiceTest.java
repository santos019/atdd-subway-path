package nextstep.subway.unit;

import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.exception.LineNotFoundException;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.section.service.SectionService;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.station.exception.StationNotFoundException;
import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static nextstep.subway.util.LineStep.지하철_노선_생성;
import static nextstep.subway.util.StationStep.지하철_역_등록;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class SectionServiceTest {

    @Autowired
    DatabaseCleanup databaseCleanup;

    @Autowired
    SectionService sectionService;

    private StationResponse 강남역;
    private StationResponse 선릉역;
    private StationResponse 삼성역;
    private StationResponse 언주역;
    private StationResponse 논현역;
    private LineResponse 신분당선;

    @BeforeEach
    public void setup() {

        databaseCleanup.execute();

        강남역 = 지하철_역_등록("강남역");
        선릉역 = 지하철_역_등록("선릉역");
        삼성역 = 지하철_역_등록("삼성역");
        언주역 = 지하철_역_등록("언주역");
        논현역 = 지하철_역_등록("논현역");

        신분당선 = 지하철_노선_생성("신분당선", "Red", 강남역.getId(), 선릉역.getId(), 10L);
    }

    @Test
    @DisplayName("새로운 구간을 첫번째 구간에 생성한다.")
    @Transactional
    @Rollback
    public void createSection_first() {
        // when
        SectionRequest 생성_요청 = SectionRequest.of(삼성역.getId(), 강남역.getId(), 5L);
        SectionResponse 생성_응답 = sectionService.createSection(신분당선.getId(), 생성_요청);

        // then
        Assertions.assertEquals(생성_응답.getLineId(), 신분당선.getId());
        Assertions.assertEquals(생성_응답.getUpStationResponse().getId(), 삼성역.getId());
        Assertions.assertEquals(생성_응답.getUpStationResponse().getName(), 삼성역.getName());
        Assertions.assertEquals(생성_응답.getDownStationResponse().getId(), 강남역.getId());
        Assertions.assertEquals(생성_응답.getDownStationResponse().getName(), 강남역.getName());
    }

    @Test
    @DisplayName("새로운 구간을 중간 구간에 생성한다.")
    public void createSection_middle() {
        // given
        sectionService.createSection(신분당선.getId(), SectionRequest.of(선릉역.getId(), 삼성역.getId(), 5L));

        // when
        SectionRequest 생성_요청 = SectionRequest.of(선릉역.getId(), 언주역.getId(), 1L);
        SectionResponse 생성_응답 = sectionService.createSection(신분당선.getId(), 생성_요청);

        // then
        Assertions.assertEquals(생성_응답.getLineId(), 신분당선.getId());
        Assertions.assertEquals(생성_응답.getUpStationResponse().getId(), 선릉역.getId());
        Assertions.assertEquals(생성_응답.getUpStationResponse().getName(), 선릉역.getName());
        Assertions.assertEquals(생성_응답.getDownStationResponse().getId(), 언주역.getId());
        Assertions.assertEquals(생성_응답.getDownStationResponse().getName(), 언주역.getName());
        Assertions.assertEquals(생성_응답.getDistance(), 1L);
    }

    @Test
    @DisplayName("새로운 구간을 마지막 구간에 생성한다.")
    public void createSection_last() {
        // given
        SectionRequest 생성_요청 = SectionRequest.of(선릉역.getId(), 언주역.getId(), 1L);

        // when
        SectionResponse 생성_응답 = sectionService.createSection(신분당선.getId(), 생성_요청);

        // then
        Assertions.assertEquals(생성_응답.getLineId(), 신분당선.getId());
        Assertions.assertEquals(생성_응답.getUpStationResponse().getId(), 선릉역.getId());
        Assertions.assertEquals(생성_응답.getUpStationResponse().getName(), 선릉역.getName());
        Assertions.assertEquals(생성_응답.getDownStationResponse().getId(), 언주역.getId());
        Assertions.assertEquals(생성_응답.getDownStationResponse().getName(), 언주역.getName());
        Assertions.assertEquals(생성_응답.getDistance(), 1L);
    }

    @Test
    @DisplayName("새로운 구간의 lineId를 찾을 수 없다.")
    public void createSection_fail_lineId_cannot_found() {
        // given
        SectionRequest 생성_요청 = SectionRequest.of(선릉역.getId(), 언주역.getId(), 1L);

        // when & then
        Assertions.assertThrows(LineNotFoundException.class, () -> sectionService.createSection(2L, 생성_요청))
                .getMessage().equals("노선을 찾을 수 없습니다.");

    }

    @Test
    @DisplayName("새로운 구간의 upStation을 찾을 수 없다.")
    public void createSection_fail_upStation_cannot_found() {
        // given
        SectionRequest 생성_요청 = SectionRequest.of(10L, 언주역.getId(), 1L);

        // when & then
        Assertions.assertThrows(StationNotFoundException.class, () -> sectionService.createSection(신분당선.getId(), 생성_요청))
                .getMessage().equals("역을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("새로운 구간의 downStation을 찾을 수 없다.")
    public void createSection_fail_downStation_cannot_found() {
        // given
        SectionRequest 생성_요청 = SectionRequest.of(언주역.getId(), 10L, 1L);

        // when & then
        Assertions.assertThrows(StationNotFoundException.class, () -> sectionService.createSection(신분당선.getId(), 생성_요청))
                .getMessage().equals("역을 찾을 수 없습니다.");
    }
}
