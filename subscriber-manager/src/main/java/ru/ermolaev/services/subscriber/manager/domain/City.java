package ru.ermolaev.services.subscriber.manager.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Getter
@Setter
@Entity
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "city-entries-cache")
@Table(name = "CITIES")
public class City {

    @Id
    @Column(name = "CITY_ID")
    @SequenceGenerator(name = "CITY_ID_SEQ", sequenceName = "CITY_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CITY_ID_SEQ")
    private Long id;

    @Column(name = "EXTERNAL_ID", nullable = false,
            unique = true, updatable = false)
    private Long externalId;

    @Column(name = "NAME", nullable = false, unique = true)
    private String name;

}
