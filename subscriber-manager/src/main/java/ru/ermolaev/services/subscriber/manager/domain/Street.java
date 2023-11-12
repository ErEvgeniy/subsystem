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
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "street-entries-cache")
@Table(name = "STREETS")
public class Street {

    @Id
    @Column(name = "STREET_ID")
    @SequenceGenerator(name = "STREET_ID_SEQ", sequenceName = "STREET_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "STREET_ID_SEQ")
    private Long id;

    @Column(name = "EXTERNAL_ID", nullable = false,
            unique = true, updatable = false)
    private Long externalId;

    @Column(name = "NAME", nullable = false, unique = true)
    private String name;

}
