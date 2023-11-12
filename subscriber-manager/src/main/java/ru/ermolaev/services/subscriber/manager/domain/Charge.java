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

}
