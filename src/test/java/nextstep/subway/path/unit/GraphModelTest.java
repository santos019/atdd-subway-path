package nextstep.subway.path.unit;

import nextstep.subway.line.entity.Line;
import nextstep.subway.path.dto.GraphModel;
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
        Line line = new Line(1L, "신분당선", "red", 15L, sections);
        GraphModel graphModel = new GraphModel(1L, 2L);
        graphModel.createGraphModel(Collections.singletonList(line));

        // when
        Path path = graphModel.findShortestPath();

        // then
        assertAll(
                () -> assertNotNull(path),
                () -> assertEquals(5.0, path.getWeight()),
                () -> assertEquals(List.of(강남역.getId(), 역삼역.getId()), path.getVertexList())
        );
    }

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
}
