package nextstep.subway.path.unit;

import nextstep.subway.line.entity.Line;
import nextstep.subway.path.domain.GraphModel;
import nextstep.subway.path.dto.Path;
import nextstep.subway.path.exception.PathException;
import nextstep.subway.section.entity.Section;
import nextstep.subway.section.entity.Sections;
import nextstep.subway.station.entity.Station;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static nextstep.subway.common.constant.ErrorCode.PATH_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;

public class GraphModelTest {

    Station 강남역;
    Station 역삼역;

    @BeforeEach
    public void setup() {
        강남역 = new Station(1L, "강남역");
        역삼역 = new Station(2L, "역삼역");
    }

    @DisplayName("[createGraphModel] graph를 생성한다.")
    @Test
    void createGraphModel_success() {
        // given
        Section section = new Section(강남역, 역삼역, 5L);
        Sections sections = new Sections(List.of(section));
        Line line = new Line(1L, "신분당선", "red", 15L, sections);
        GraphModel graphModel = new GraphModel(1L, 2L);

        // when
        graphModel.createGraphModel(Collections.singletonList(line));

        // then
        WeightedMultigraph<Long, DefaultWeightedEdge> graph = graphModel.getGraph();
        DefaultWeightedEdge edge = graph.getEdge(강남역.getId(), 역삼역.getId());

        assertAll(
                () -> assertTrue(graph.containsVertex(강남역.getId())),
                () -> assertTrue(graph.containsVertex(역삼역.getId())),
                () -> assertTrue(graph.containsEdge(강남역.getId(), 역삼역.getId())),
                () -> assertNotNull(edge),
                () -> assertEquals(graph.getEdgeWeight(edge), 5.0)
        );
    }

    @DisplayName("[createGraphModel] 출발역과 도착역이 같은 section을 가진 linelist는 예외를 발생시킨다.")
    @Test
    void createGraphModel_fail() {
        // given
        Section section = new Section(강남역, 강남역, 5L);
        Sections sections = new Sections(List.of(section));
        Line line = new Line(1L, "신분당선", "red", 15L, sections);
        GraphModel graphModel = new GraphModel(1L, 2L);

        // when & then
        Assertions.assertThrows(PathException.class, () -> graphModel.createGraphModel(Collections.singletonList(line)))
                .getMessage().equals(PATH_NOT_FOUND.getDescription());
    }

    @DisplayName("[createGraphModel] Linelist가 비어있으면 예외가 발생한다.")
    @Test
    void createGraphModel_fail2() {
        // given
        GraphModel graphModel = new GraphModel(1L, 2L);

        // when & then
        Assertions.assertThrows(PathException.class, () -> graphModel.createGraphModel(List.of()))
                .getMessage().equals(PATH_NOT_FOUND.getDescription());
    }

    @DisplayName("[createGraphModel] Linelist의 Sections가 비어있으면 예외가 발생한다.")
    @Test
    void createGraphModel_fail3() {
        // given
        Sections sections = new Sections(List.of());
        Line line = new Line(1L, "신분당선", "red", 15L, sections);
        GraphModel graphModel = new GraphModel(1L, 2L);

        // when & then
        Assertions.assertThrows(PathException.class, () -> graphModel.createGraphModel(Collections.singletonList(line)))
                .getMessage().equals(PATH_NOT_FOUND.getDescription());
    }

    @DisplayName("[findShortestPath] Path를 생성한다.")
    @Test
    void findShortestPath_success() {
        // given
        Section section = new Section(강남역, 역삼역, 5L);
        Sections sections = new Sections(List.of(section));
        Line 신분당선 = new Line(1L, "신분당선", "red", 15L, sections);
        List<Line> 지하철_리스트 = Collections.singletonList(신분당선);
        GraphModel graphModel = new GraphModel(1L, 2L);
        graphModel.createGraphModel(지하철_리스트);

        // when
        Path path = graphModel.findPath(지하철_리스트);

        // then
        assertAll(
                () -> assertNotNull(path),
                () -> assertEquals(5.0, path.getWeight()),
                () -> assertEquals(List.of(강남역, 역삼역), path.getStationList())
        );
    }

    @DisplayName("[findShortestPath] lineList가 비어있으면 예외가 발생한다..")
    @Test
    void findShortestPath_fail1() {
        // given
        Section section = new Section(강남역, 역삼역, 5L);
        Sections sections = new Sections(List.of(section));
        Line 신분당선 = new Line(1L, "신분당선", "red", 15L, sections);
        List<Line> 지하철_리스트 = Collections.singletonList(신분당선);
        GraphModel graphModel = new GraphModel(1L, 2L);
        graphModel.createGraphModel(지하철_리스트);

        // when
        Path path = graphModel.findPath(지하철_리스트);

        // then
        assertAll(
                () -> assertNotNull(path),
                () -> assertEquals(5.0, path.getWeight()),
                () -> assertEquals(List.of(강남역, 역삼역), path.getStationList())
        );
    }

    @DisplayName("[findShortestPath] vertexList가 비어있으면 예외가 발생한다. - null일 경우")
    @Test
    void findShortestPath_fail2() {
        // given
        Section section = new Section(강남역, 역삼역, 5L);
        Sections sections = new Sections(List.of(section));
        Line 신분당선 = new Line(1L, "신분당선", "red", 15L, sections);
        List<Line> 지하철_리스트 = Collections.singletonList(신분당선);
        GraphModel graphModel = new GraphModel(1L, 2L);

        assertAll(
                () -> assertThrows(PathException.class, () -> graphModel.findPath(지하철_리스트))
                        .getMessage().equals(PATH_NOT_FOUND.getDescription())
        );

    }

    @DisplayName("[getStationList] lineList와 stationId를 통해 StationList를 생성한다.")
    @Test
    void getStationList_success() {}

    @DisplayName("[getStationList] lineList가 비어있으면 비어 있는 StationList를 생성한다.")
    @Test
    void getStationList_fail() {}

    @DisplayName("[getStationList] lineList에 StationId가 없다면 예외가 발생한다.")
    @Test
    void getStationList_fail2() {}

    @DisplayName("[addSectionsToGraph] line을 graph의 Edge에 추가한다.")
    @Test
    public void addSectionsToGraph_success() {
        // given
        Section section = new Section(강남역, 역삼역, 5L);
        Sections sections = new Sections(List.of(section));
        Line line = new Line(1L, "신분당선", "red", 15L, sections);
        GraphModel graphModel = new GraphModel(1L, 2L);

        // when
        graphModel.addSectionsToGraph(line);

        // then
        WeightedMultigraph<Long, DefaultWeightedEdge> graph = graphModel.getGraph();
        DefaultWeightedEdge edge = graph.getEdge(강남역.getId(), 역삼역.getId());

        assertAll(
                () -> assertTrue(graph.containsVertex(강남역.getId())),
                () -> assertTrue(graph.containsVertex(역삼역.getId())),
                () -> assertNotNull(edge),
                () -> assertEquals(graph.getEdgeWeight(edge), 5.0),
                () -> assertTrue(graph.containsEdge(강남역.getId(), 역삼역.getId()))
        );
    }

    @DisplayName("[addSectionsToGraph] Sections가 비어 있는 Line을 graph의 Edge에 추가하면 예외가 발생한다.")
    @Test
    public void addSectionsToGraph_fail() {
        // given
        Sections sections = new Sections(List.of());
        Line line = new Line(1L, "신분당선", "red", 15L, sections);
        GraphModel graphModel = new GraphModel(1L, 2L);

        // when & then
        Assertions.assertThrows(PathException.class, () -> graphModel.addSectionsToGraph(line))
                .getMessage().equals(PATH_NOT_FOUND.getDescription());
    }

    @DisplayName("[addSectionsToGraph] 동일한 StationId를 가지고 있는 Section을 graph의 Edge에 추가하면 예외가 발생한다.")
    @Test
    public void addSectionsToGraph_fail2() {
        // given
        Section section = new Section(강남역, 강남역, 5L);
        Sections sections = new Sections(List.of(section));
        Line line = new Line(1L, "신분당선", "red", 15L, sections);
        GraphModel graphModel = new GraphModel(1L, 2L);

        // when & then
        Assertions.assertThrows(PathException.class, () -> graphModel.addSectionsToGraph(line))
                .getMessage().equals(PATH_NOT_FOUND.getDescription());
    }

    @DisplayName("[addEdge] 새로운 Edge를 생성한다.")
    @Test
    public void addEdge_success() {
        // given
        GraphModel graphModel = new GraphModel(1L, 2L);

        graphModel.addEdge(강남역.getId(), 역삼역.getId(), 20.0);

        // then
        WeightedMultigraph<Long, DefaultWeightedEdge> graph = graphModel.getGraph();
        DefaultWeightedEdge edge = graph.getEdge(강남역.getId(), 역삼역.getId());

        assertAll(
                () -> assertTrue(graph.containsVertex(강남역.getId())),
                () -> assertTrue(graph.containsVertex(역삼역.getId())),
                () -> assertTrue(edge != null),
                () -> assertTrue(graph.getEdgeWeight(edge) == 20.0)
        );
    }

    @DisplayName("[addEdge] 동일한 source와 target으로는 Edge를 생성할 수 없다.")
    @Test
    public void addEdge_fail() {
        // given
        GraphModel graphModel = new GraphModel(1L, 2L);

        // then
        Assertions.assertThrows(PathException.class, () -> graphModel.addEdge(4L, 4L, 20.0))
                .getMessage().equals(PATH_NOT_FOUND.getDescription());
    }

    @DisplayName("[validateDuplicate] 동일하지 않는 source와 target을 인자로 주면 예외가 발생하지 않는다.")
    @Test
    public void validateDuplicate_success() {
        // give
        Long source = 1L;
        Long target = 2L;
        GraphModel graphModel = new GraphModel(1L, 2L);

        // when & then
        Assertions.assertDoesNotThrow(() -> graphModel.validateDuplicate(source, target));
    }

    @DisplayName("[validateDuplicate] 동일한 source와 target을 인자로 주면 예외가 발생한다.")
    @Test
    public void validateDuplicate_fail() {
        // give
        Long source = 1L;
        Long target = 1L;
        GraphModel graphModel = new GraphModel(1L, 2L);

        // when & then
        Assertions.assertThrows(PathException.class, () -> graphModel.validateDuplicate(source, target))
                .getMessage().equals(PATH_NOT_FOUND.getDescription());
    }

    @DisplayName("[getStation] stationId에 해당하는 Station을 찾는다. upStation으로 찾는다.")
    @Test
    void getStation_success() {
        // when
        Section section = new Section(강남역, 역삼역, 5L);
        Sections sections = new Sections(List.of(section));
        Line 신분당선 = new Line(1L, "신분당선", "red", 15L, sections);
        List<Line> 지하철_목록 = List.of(신분당선);
        GraphModel graphModel = new GraphModel(1L, 2L);

        var 찾은_역 = graphModel.getStation(지하철_목록, 강남역.getId());

        // then
        assertAll(
                () -> assertEquals(찾은_역, 강남역)
        );
    }

    @DisplayName("[getStation] stationId에 해당하는 Station을 찾는다. downStation으로 찾는다.")
    @Test
    void getStation_success2() {
        // when
        Section section = new Section(강남역, 역삼역, 5L);
        Sections sections = new Sections(List.of(section));
        Line 신분당선 = new Line(1L, "신분당선", "red", 15L, sections);
        List<Line> 지하철_목록 = List.of(신분당선);
        GraphModel graphModel = new GraphModel(1L, 2L);

        var 찾은_역 = graphModel.getStation(지하철_목록, 역삼역.getId());

        // then
        assertAll(
                () -> assertEquals(찾은_역, 역삼역)
        );
    }

    @DisplayName("[getStation] stationId에 해당하는 Station을 찾지 못하면 예외가 발생한다.")
    @Test
    void getStation_fail1() {
        // when
        Section section = new Section(강남역, 역삼역, 5L);
        Sections sections = new Sections(List.of(section));
        Line 신분당선 = new Line(1L, "신분당선", "red", 15L, sections);
        List<Line> 지하철_목록 = List.of(신분당선);
        GraphModel graphModel = new GraphModel(1L, 2L);

        // then
        assertThrows(PathException.class, () -> graphModel.getStation(지하철_목록, 3L))
                .getMessage().equals(PATH_NOT_FOUND.getDescription());
    }

    @DisplayName("[getStation] lineList가 비어 있으면 예외가 발생한다.")
    @Test
    void getStation_fail2() {
        // when
        GraphModel graphModel = new GraphModel(1L, 2L);

        // when & then
        assertThrows(PathException.class, () -> graphModel.getStation(List.of(), 3L))
                .getMessage().equals(PATH_NOT_FOUND.getDescription());
    }
}
