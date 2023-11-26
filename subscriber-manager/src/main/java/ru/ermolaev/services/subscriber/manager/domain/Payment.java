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
import java.util.Objects;

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

    @Column(name = "EXTERNAL_ID", nullable = false,
            unique = true, updatable = false)
    private Long externalId;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Payment payment)) {
            return false;
        }
        return Objects.equals(id, payment.id) &&
                Objects.equals(externalId, payment.externalId) &&
                Objects.equals(paymentDate, payment.paymentDate) &&
                Objects.equals(paymentChannel, payment.paymentChannel) &&
                Objects.equals(subscriber, payment.subscriber) &&
                Objects.equals(amount, payment.amount) &&
                Objects.equals(period, payment.period) &&
                Objects.equals(comment, payment.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, externalId, paymentDate, paymentChannel, subscriber, amount, period, comment);
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", externalId=" + externalId +
                ", paymentDate=" + paymentDate +
                ", paymentChannel=" + paymentChannel +
                ", subscriber=" + subscriber +
                ", amount=" + amount +
                ", period='" + period + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }

}
