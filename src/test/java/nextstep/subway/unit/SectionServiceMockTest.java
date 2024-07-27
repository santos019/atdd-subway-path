package nextstep.subway.unit;

import nextstep.subway.line.entity.Line;
import nextstep.subway.line.exception.LineNotFoundException;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.line.service.LineService;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.section.entity.Section;
import nextstep.subway.section.entity.Sections;
import nextstep.subway.section.repository.SectionRepository;
import nextstep.subway.section.service.SectionService;
import nextstep.subway.station.entity.Station;
import nextstep.subway.station.exception.StationNotFoundException;
import nextstep.subway.station.repository.StationRepository;
import nextstep.subway.station.service.StationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SectionServiceMockTest {

    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationRepository stationRepository;
    @Mock
    private SectionRepository sectionRepository;
    private StationService stationService;
    private LineService lineService;
    private SectionService sectionService;

    private Station 강남역;
    private Station 선릉역;
    private Station 삼성역;
    private Station 언주역;
    private Sections 신분당선_구간 = new Sections();
    private Section 강남역_선릉역_구간;
    private Line 신분당선;

    @BeforeEach
    public void setup() {
        stationService = new StationService(stationRepository);
        lineService = new LineService(lineRepository, stationService);
        sectionService = new SectionService(sectionRepository, stationService, lineService);
        강남역 = Station.of(1L, "강남역");
        선릉역 = Station.of(2L, "선릉역");
        삼성역 = Station.of(3L, "삼성역");
        언주역 = Station.of(4L, "언주역");

        강남역_선릉역_구간 = Section.of(강남역, 선릉역, 1L);
        신분당선_구간.addSection(강남역_선릉역_구간);
        신분당선 = Line.of(1L, "신분당선", "Red", 10L, 신분당선_구간);
    }

    @Test
    @DisplayName("새로운 구간을 첫번째 구간에 생성한다.")
    public void createSection_first() {
        // given
        when(lineRepository.findById(1L)).thenReturn(Optional.ofNullable(신분당선));
        when(stationRepository.findById(1L)).thenReturn(Optional.ofNullable(강남역));
        when(stationRepository.findById(3L)).thenReturn(Optional.ofNullable(삼성역));
        when(lineRepository.save(신분당선)).thenReturn(신분당선);

        // when
        var 생성_요청 = SectionRequest.of(삼성역.getId(), 강남역.getId(), 5L);
        var 생성_응답 = sectionService.createSection(신분당선.getId(), 생성_요청);

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
        when(lineRepository.findById(1L)).thenReturn(Optional.ofNullable(신분당선));
        when(stationRepository.findById(2L)).thenReturn(Optional.ofNullable(선릉역));
        when(stationRepository.findById(3L)).thenReturn(Optional.ofNullable(삼성역));
        when(stationRepository.findById(4L)).thenReturn(Optional.ofNullable(언주역));
        when(lineRepository.save(신분당선)).thenReturn(신분당선);

        sectionService.createSection(신분당선.getId(), SectionRequest.of(선릉역.getId(), 삼성역.getId(), 5L));

        // when
        var 생성_요청 = SectionRequest.of(선릉역.getId(), 언주역.getId(), 1L);
        var 생성_응답 = sectionService.createSection(신분당선.getId(), 생성_요청);

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
        when(lineRepository.findById(1L)).thenReturn(Optional.ofNullable(신분당선));
        when(stationRepository.findById(2L)).thenReturn(Optional.ofNullable(선릉역));
        when(stationRepository.findById(4L)).thenReturn(Optional.ofNullable(언주역));
        when(lineRepository.save(신분당선)).thenReturn(신분당선);

        var 생성_요청 = SectionRequest.of(선릉역.getId(), 언주역.getId(), 1L);

        // when
        var 생성_응답 = sectionService.createSection(신분당선.getId(), 생성_요청);

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
        when(lineRepository.findById(2L)).thenReturn(Optional.ofNullable(null));

        var 생성_요청 = SectionRequest.of(선릉역.getId(), 언주역.getId(), 1L);

        // when & then
        Assertions.assertThrows(LineNotFoundException.class, () -> sectionService.createSection(2L, 생성_요청))
                .getMessage().equals("노선을 찾을 수 없습니다.");

    }

    @Test
    @DisplayName("새로운 구간의 upStation을 찾을 수 없다.")
    public void createSection_fail_upStation_cannot_found() {
        // given
        when(lineRepository.findById(1L)).thenReturn(Optional.ofNullable(신분당선));
        when(stationRepository.findById(10L)).thenReturn(Optional.ofNullable(null));

        var 생성_요청 = SectionRequest.of(10L, 언주역.getId(), 1L);

        // when & then
        Assertions.assertThrows(StationNotFoundException.class, () -> sectionService.createSection(신분당선.getId(), 생성_요청))
                .getMessage().equals("역을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("새로운 구간의 downStation을 찾을 수 없다.")
    public void createSection_fail_downStation_cannot_found() {
        // given
        when(lineRepository.findById(1L)).thenReturn(Optional.ofNullable(신분당선));
        when(stationRepository.findById(4L)).thenReturn(Optional.ofNullable(언주역));
        when(stationRepository.findById(10L)).thenReturn(Optional.ofNullable(null));

        var 생성_요청 = SectionRequest.of(언주역.getId(), 10L, 1L);

        // when & then
        Assertions.assertThrows(StationNotFoundException.class, () -> sectionService.createSection(신분당선.getId(), 생성_요청))
                .getMessage().equals("역을 찾을 수 없습니다.");
    }

}
