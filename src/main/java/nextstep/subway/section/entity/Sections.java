package nextstep.subway.section.entity;

import nextstep.subway.section.exception.SectionException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static nextstep.subway.common.constant.ErrorCode.*;

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

    public void addSection(Section section) {
        if (!sections.isEmpty()) {
            Section lastSection = getLastSection();
            if (!section.getUpStation().equals(lastSection.getDownStation())) {
                throw new SectionException(String.valueOf(SECTION_NOT_MATCH));
            }
            if (sections.stream().anyMatch(s -> s.getDownStation().equals(section.getDownStation()))) {
                throw new SectionException(String.valueOf(SECTION_ALREADY_EXIST));
            }
            lastSection.setNextSection(section);
            section.setPreviousSection(lastSection);
        }
        sections.add(section);
    }

    public void removeSection(Section section) {
        if (sections.size() <= 1) {
            throw new SectionException(String.valueOf(SECTION_NOT_PERMISSION_COUNT_TOO_LOW));
        }
        if (!getLastSection().getDownStation().equals(section.getDownStation())) {
            throw new SectionException(String.valueOf(SECTION_NOT_PERMISSION_NOT_LAST_DESCENDING_STATION));
        }

        Section previous = section.getPreviousSection();
        Section next = section.getNextSection();
        if (previous != null) {
            previous.setNextSection(next);
        }
        if (next != null) {
            next.setPreviousSection(previous);
        }
        sections.remove(section);
    }

    public Section getFirstSection() {
        return sections.stream()
                .filter(section -> section.getPreviousSection() == null)
                .findFirst()
                .orElseThrow(() -> new SectionException(String.valueOf(SECTION_FIRST_STATION_NOT_FOUND)));
    }

    public Section getLastSection() {
        return sections.stream()
                .filter(section -> section.getNextSection() == null)
                .findFirst()
                .orElseThrow(() -> new SectionException(String.valueOf(SECTION_LAST_STATION_NOT_FOUND)));
    }
}
