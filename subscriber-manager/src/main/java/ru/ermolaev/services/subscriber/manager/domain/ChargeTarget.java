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

import java.util.Objects;

@Getter
@Setter
@Entity
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "charge-target-entries-cache")
@Table(name = "CHARGE_TARGETS")
public class ChargeTarget {

    @Id
    @Column(name = "CHARGE_TARGET_ID")
    @SequenceGenerator(name = "CHARGE_TARGET_ID_SEQ", sequenceName = "CHARGE_TARGET_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CHARGE_TARGET_ID_SEQ")
    private Long id;

    @Column(name = "EXTERNAL_ID", nullable = false,
            unique = true, updatable = false)
    private Long externalId;

    @Column(name = "NAME", nullable = false, unique = true)
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChargeTarget that)) {
            return false;
        }
        return Objects.equals(id, that.id) &&
                Objects.equals(externalId, that.externalId) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, externalId, name);
    }

    @Override
    public String toString() {
        return "ChargeTarget{" +
                "id=" + id +
                ", externalId=" + externalId +
                ", name='" + name + '\'' +
                '}';
    }

}
