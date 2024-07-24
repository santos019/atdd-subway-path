package nextstep.subway.station.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import nextstep.subway.station.entity.Station;

public interface StationRepository extends JpaRepository<Station, Long> {
}