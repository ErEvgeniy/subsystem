package ru.ermolaev.services.subscriber.manager.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.time.LocalDate;
import java.util.List;

@NamedEntityGraph(
        name = "subscriber-and-address-graph",
        attributeNodes = {
                @NamedAttributeNode("city"),
                @NamedAttributeNode("street")
        }
)
@Getter
@Setter
@Entity
@Table(name = "SUBSCRIBERS")
public class Subscriber {

    @Id
    @Column(name = "SUBSCRIBER_ID")
    @SequenceGenerator(name = "SUBSCRIBER_ID_SEQ", sequenceName = "SUBSCRIBER_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SUBSCRIBER_ID_SEQ")
    private Long id;

    @Column(name = "FIRSTNAME", nullable = false)
    private String firstname;

    @Column(name = "PATRONYMIC")
    private String patronymic;

    @Column(name = "LASTNAME", nullable = false)
    private String lastname;

    @Column(name = "CONTRACT_NUMBER", unique = true)
    private String contractNumber;

    @Column(name = "ACCOUNT_NUMBER", unique = true)
    private String accountNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CITY_ID")
    private City city;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STREET_ID")
    private Street street;

    @Column(name = "HOUSE")
    private Integer house;

    @Column(name = "FLAT")
    private Integer flat;

    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "BALANCE")
    private Float balance = (float) 0;

    @Fetch(FetchMode.SELECT)
    @BatchSize(size = 10)
    @OneToMany(
            targetEntity = Payment.class,
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "SUBSCRIBER_ID")
    private List<Payment> payments;

    @Fetch(FetchMode.SELECT)
    @BatchSize(size = 10)
    @OneToMany(
            targetEntity = Charge.class,
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "SUBSCRIBER_ID")
    private List<Charge> charges;

    @Column(name = "IS_ACTIVE")
    private Boolean isActive;

    @Column(name = "CONNECTION_DATE")
    private LocalDate connectionDate;

    @Column(name = "EXTERNAL_ID", nullable = false,
            unique = true, updatable = false)
    private Long externalId;

}
