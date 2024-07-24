package nextstep.subway.section.entity;

import nextstep.subway.section.exception.SectionException;
import nextstep.subway.station.entity.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static nextstep.subway.common.constant.ErrorCode.*;
import static nextstep.subway.converter.LineConverter.convertToStationIds;

@Embeddable
public class Sections {

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "LINE_ID")
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Section> getSections() {
        return sections;
    }

    public Section getFirstSection() {
        return sections.stream()
                .min(Comparator.comparing(Section::getPosition)).get();
    }

    public Section getLastSection() {
        return sections.stream()
                .max(Comparator.comparing(Section::getPosition)).get();
    }

    public Station getFirstStation() {
        return sections.stream()
                .min(Comparator.comparing(Section::getPosition))
                .get().getUpStation();
    }

    public Station getLastStation() {
        return sections.stream()
                .max(Comparator.comparing(Section::getPosition))
                .get().getDownStation();
    }

    public Long getCurrentSectionsPosition() {
        return (long) sections.size() - 1;
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    public void removeSection(Section section) {
        sections.remove(section);
    }

    public void validateCreateSection(Sections sections, Long upStationId, Long downStationId) {
        List<Long> sectionsStation = convertToStationIds(sections);

        validateSectionsLastDownStationIdIsSameUpStationId(sections.getLastStation().getId(), upStationId);
        validateSectionDoesNotContainStation(sectionsStation, downStationId);
    }

    public void validateSectionsLastDownStationIdIsSameUpStationId(Long lastStationId, Long upStationId) {
        if(lastStationId != upStationId) {
            throw new SectionException(String.valueOf(SECTION_NOT_MATCH));
        }
    }

    public void validateSectionDoesNotContainStation(List<Long> sectionsStation, Long downStationId) {
        if (sectionsStation.contains(downStationId)) {
            throw new SectionException(String.valueOf(SECTION_ALREADY_EXIST));
        }
    }

    public void validateDeleteSection(Sections sections, Long stationId) {
        validateSectionsLastDownStationIsNot(sections.getLastStation().getId(), stationId);
        validateSectionsMinimumCount(sections.getSections());
    }

    public void validateSectionsLastDownStationIsNot(Long lastStationId, Long stationId) {
        if (lastStationId != stationId) {
            throw new SectionException(String.valueOf(SECTION_NOT_PERMISSION_NOT_LAST_DESCENDING_STATION));
        }
    }

    public void validateSectionsMinimumCount(List<Section> sections) {
        if (sections.size() <= 1) {
            throw new SectionException(String.valueOf(SECTION_NOT_PERMISSION_COUNT_TOO_LOW));
        }
    }
}
