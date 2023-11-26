package ru.ermolaev.services.subscriber.manager.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;

@NamedEntityGraph(
        name = "charge-full-join-graph",
        attributeNodes = {
                @NamedAttributeNode("chargeTarget"),
                @NamedAttributeNode("subscriber")
        }
)
@Getter
@Setter
@Entity
@Table(name = "CHARGES")
public class Charge {

    @Id
    @Column(name = "CHARGE_ID")
    @SequenceGenerator(name = "CHARGE_ID_SEQ", sequenceName = "CHARGE_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CHARGE_ID_SEQ")
    private Long id;

    @Column(name = "EXTERNAL_ID", nullable = false,
            unique = true, updatable = false)
    private Long externalId;

    @Column(name = "CHARGE_DATE")
    private LocalDate chargeDate;

    @ManyToOne
    @JoinColumn(name = "CHARGE_TARGET_ID")
    private ChargeTarget chargeTarget;

    @ManyToOne
    @JoinColumn(name = "SUBSCRIBER_ID")
    private Subscriber subscriber;

    @Column(name = "AMOUNT")
    private Float amount;

    @Column(name = "PERIOD")
    private String period;

    @Column(name = "COMMENT")
    private String comment;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Charge charge)) {
            return false;
        }
        return Objects.equals(id, charge.id) &&
                Objects.equals(externalId, charge.externalId) &&
                Objects.equals(chargeDate, charge.chargeDate) &&
                Objects.equals(chargeTarget, charge.chargeTarget) &&
                Objects.equals(subscriber, charge.subscriber) &&
                Objects.equals(amount, charge.amount) &&
                Objects.equals(period, charge.period) &&
                Objects.equals(comment, charge.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, externalId, chargeDate, chargeTarget, subscriber, amount, period, comment);
    }

    @Override
    public String toString() {
        return "Charge{" +
                "id=" + id +
                ", externalId=" + externalId +
                ", chargeDate=" + chargeDate +
                ", chargeTarget=" + chargeTarget +
                ", subscriber=" + subscriber +
                ", amount=" + amount +
                ", period='" + period + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }

}
