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
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "payment-channel-entries-cache")
@Table(name = "PAYMENT_CHANNELS")
public class PaymentChannel {

    @Id
    @Column(name = "PAYMENT_CHANNEL_ID")
    @SequenceGenerator(name = "PAYMENT_CHANNEL_ID_SEQ", sequenceName = "PAYMENT_CHANNEL_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PAYMENT_CHANNEL_ID_SEQ")
    private Long id;

    @Column(name = "NAME", nullable = false, unique = true)
    private String name;

}
