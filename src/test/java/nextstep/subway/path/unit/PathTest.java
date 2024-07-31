package nextstep.subway.path.unit;

import nextstep.subway.path.dto.Path;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class PathTest {

    @DisplayName("getVertexList와 getWeight의 정상 동작을 확인한다.")
    @Test
    public void getVertexList_getWeight () {
        // given
        List<Long> vertexList = List.of(1L, 2L, 3L);
        double weight = 10.5;

        GraphPath<Long, DefaultWeightedEdge> graphPath = Mockito.mock(GraphPath.class);
        when(graphPath.getVertexList()).thenReturn(vertexList);
        when(graphPath.getWeight()).thenReturn(weight);

        // when
        Path path = new Path(graphPath);

        // then
        assertAll(
                () -> assertEquals(vertexList, path.getVertexList()),
                () -> assertEquals(weight, path.getWeight())
        );
    }
}
