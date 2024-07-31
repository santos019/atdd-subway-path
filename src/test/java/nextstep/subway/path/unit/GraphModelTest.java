//package nextstep.subway.path.unit;
//
//import nextstep.subway.path.dto.GraphModel;
//import nextstep.subway.path.exception.PathException;
//import org.jgrapht.graph.DefaultWeightedEdge;
//import org.jgrapht.graph.WeightedMultigraph;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class GraphModelTest {
//
//    @DisplayName("GraphModel에 Edge를 추가한다.")
//    @Test
//    public void addEdge() {
//        // given
//        Long source = 1L;
//        Long target = 2L;
//        double weight = 10.0;
//
//        WeightedMultigraph<Long, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
//        GraphModel graphModel = new GraphModel(graph);
//
//        // when
//        graphModel.addEdge(source, target, weight);
//
//        // then
//        assertTrue(graph.containsEdge(source, target));
//        assertEquals(weight, graph.getEdgeWeight(graph.getEdge(source, target)));
//    }
//
//    @DisplayName("GraphMode에 중복된 Edge를 추가할 수 없다. 최초로 추가된 Edge값을 가진다.")
//    @Test
//    public void addEdge_fail() {
//        // given
//        Long source = 1L;
//        Long target = 2L;
//        double weight = 10.0;
//
//        WeightedMultigraph<Long, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
//        GraphModel graphModel = new GraphModel(graph);
//
//        // when
//        graphModel.addEdge(source, target, weight);
//        graphModel.addEdge(source, target, 20.0);
//
//        // then
//        assertTrue(graph.containsEdge(source, target));
//        assertEquals(weight, graph.getEdgeWeight(graph.getEdge(source, target)));
//    }
//
//    @DisplayName("GraphModel에 vertexId가 Vertex로 포함되어 있는지 확인한다.")
//    @Test
//    public void containsVertex_success() {
//        // given
//        Long source = 1L;
//        Long target = 2L;
//        double weight = 10.0;
//
//        WeightedMultigraph<Long, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
//        GraphModel graphModel = new GraphModel(graph);
//
//        graphModel.addEdge(source, target, weight);
//
//        // when & then
////        assertAll(
////                () -> assertDoesNotThrow(() -> graphModel.containsVertex(source)),
////                () -> assertDoesNotThrow(() -> graphModel.containsVertex(target))
////        );
//    }
//
//    @DisplayName("GraphModel에 vertexId가 Vertex로 포함되어 있지 않으면 PATH_NOT_FOUND 예외가 발생한다.")
//    @Test
//    public void containsVertex_fail() {
//        // given
//        Long source = 1L;
//        Long target = 2L;
//        double weight = 10.0;
//
//        WeightedMultigraph<Long, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
//        GraphModel graphModel = new GraphModel(graph);
//
//        graphModel.addEdge(source, target, weight);
//
//        // when & then
////        Assertions.assertAll(
////                () -> assertDoesNotThrow(() -> graphModel.containsVertex(source)),
////                () -> assertThrows(PathException.class, () -> graphModel.containsVertex(3L))
////        );
//    }
//}
