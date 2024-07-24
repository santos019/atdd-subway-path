package nextstep.subway.converter;

import nextstep.subway.line.entity.Line;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.section.entity.Section;
import nextstep.subway.station.dto.StationResponse;

public class SectionConverter {

    private SectionConverter() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static SectionResponse convertToSectionResponseByLineAndSection(Line line, Section section) {

        return SectionResponse.of(line.getId(), section.getId(), new StationResponse(section.getUpStation().getId(), section.getUpStation().getName()),
                new StationResponse(section.getDownStation().getId(), section.getDownStation().getName()), section.getDistance());

    }
}
