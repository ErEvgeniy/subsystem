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
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@NamedEntityGraph(
        name = "notification-and-subscriber-graph",
        attributeNodes = {
                @NamedAttributeNode("subscriber")
        }
)
@Getter
@Setter
@Entity
@Table(name = "NOTIFICATIONS")
public class Notification {

    @Id
    @Column(name = "NOTIFICATION_ID")
    @SequenceGenerator(name = "NOTIFICATION_ID_SEQ", sequenceName = "NOTIFICATION_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "NOTIFICATION_ID_SEQ")
    private Long id;

    @Column(name = "SENT_DATE")
    private LocalDateTime sentDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUBSCRIBER_ID")
    private Subscriber subscriber;

    @Column(name = "STATUS", nullable = false)
    private String status;

    @Column(name = "MESSAGE", nullable = false)
    private String message;

    @Column(name = "CHANNEL", nullable = false)
    private String channel;

    @Column(name = "DESTINATION", nullable = false)
    private String destination;

    @Column(name = "REASON")
    private String reason;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Notification that)) {
            return false;
        }
        return Objects.equals(id, that.id) &&
                Objects.equals(sentDate, that.sentDate) &&
                Objects.equals(subscriber, that.subscriber) &&
                Objects.equals(status, that.status) &&
                Objects.equals(message, that.message) &&
                Objects.equals(channel, that.channel) &&
                Objects.equals(destination, that.destination) &&
                Objects.equals(reason, that.reason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sentDate, subscriber, status, message, channel, destination, reason);
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", sentDate=" + sentDate +
                ", subscriber=" + subscriber +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", channel='" + channel + '\'' +
                ", destination='" + destination + '\'' +
                ", reason='" + reason + '\'' +
                '}';
    }

}
