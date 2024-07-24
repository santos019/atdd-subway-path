package nextstep.subway.station.entity;

import javax.persistence.*;

@Entity
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STATION_ID")
    private Long id;

    @Column(length = 20, nullable = false)
    private String name;

    protected Station() {
    }

    public Station(String name) {
        this.name = name;
    }

    public static Station of (String name) {
        return new Station(name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
