package nextstep.subway.section.entity;

import nextstep.subway.section.exception.SectionException;
import nextstep.subway.station.entity.Station;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

import static nextstep.subway.common.constant.ErrorCode.SECTION_DISTANCE_TOO_SHORT;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UP_STATION_ID")
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DOWN_STATION_ID")
    private Station downStation;

    @NotNull
    private Long distance;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "PREVIOUS_SECTION_ID")
    private Section previousSection;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "NEXT_SECTION_ID")
    private Section nextSection;

    public Section() {
    }

    public Section(Station upStation, Station downStation, Long distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section of(Station upStation, Station downStation, Long distance) {
        if(distance < 1) {
            throw new SectionException(String.valueOf(SECTION_DISTANCE_TOO_SHORT));
        }

        return new Section(upStation, downStation, distance);
    }

    public Long getId() {
        return id;
    }

    public List<Station> getStations() {
        return List.of(upStation, downStation);
    }

    public Station getUpStation() {
        return this.upStation;
    }

    public Station getDownStation() {
        return this.downStation;
    }

    public Long getDistance() {
        return this.distance;
    }

    public Section getPreviousSection() {
        return previousSection;
    }

    public void setPreviousSection(Section previousSection) {
        this.previousSection = previousSection;
    }

    public void setUpStation(Station upStation) {
        this.upStation = upStation;
    }

    public void setDistance(Long distance) {
        this.distance = distance;
    }

    public Section getNextSection() {
        return nextSection;
    }

    public void setNextSection(Section nextSection) {
        this.nextSection = nextSection;
    }

    public void setDownStation(Station downStation) {
        this.downStation = downStation;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Section section = (Section) obj;
        return Objects.equals(id, section.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
