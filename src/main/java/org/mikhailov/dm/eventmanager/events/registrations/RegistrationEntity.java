package org.mikhailov.dm.eventmanager.events.registrations;

import jakarta.persistence.*;
import org.mikhailov.dm.eventmanager.events.EventEntity;

@Entity
@Table
public class RegistrationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    @ManyToOne
    @JoinColumn(name = "event_id")
    private EventEntity event;

    public RegistrationEntity(Long id, Long userId, EventEntity event) {
        this.id = id;
        this.userId = userId;
        this.event = event;
    }

    public RegistrationEntity() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public EventEntity getEvent() {
        return event;
    }

    public void setEvent(EventEntity event) {
        this.event = event;
    }
}
