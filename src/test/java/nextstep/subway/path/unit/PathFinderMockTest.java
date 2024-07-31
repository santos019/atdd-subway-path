package nextstep.subway.path.unit;

import nextstep.subway.path.service.PathFinder;
import nextstep.subway.station.service.StationService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PathFinderMockTest {

    @Mock
    private StationService stationService;

    @InjectMocks
    private PathFinder pathFinder;



}
