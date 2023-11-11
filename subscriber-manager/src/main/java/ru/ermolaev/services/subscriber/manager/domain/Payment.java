package ru.ermolaev.services.subscriber.manager.domain;

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
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NamedEntityGraph(
        name = "payment-full-join-graph",
        attributeNodes = {
                @NamedAttributeNode("paymentChannel"),
                @NamedAttributeNode("subscriber")
        }
)
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "PAYMENTS")
public class Payment {

    @Id
    @Column(name = "PAYMENT_ID")
    @SequenceGenerator(name = "PAYMENT_ID_SEQ", sequenceName = "PAYMENT_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PAYMENT_ID_SEQ")
    private Long id;

    @Column(name = "PAYMENT_DATE")
    private LocalDate paymentDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PAYMENT_CHANNEL_ID")
    private PaymentChannel paymentChannel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUBSCRIBER_ID")
    private Subscriber subscriber;

    @Column(name = "AMOUNT")
    private Float amount;

    @Column(name = "PERIOD")
    private String period;

    @Column(name = "COMMENT")
    private String comment;

}
