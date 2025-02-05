package org.mikhailov.dm.eventmanager.events;

import jakarta.persistence.*;
import org.mikhailov.dm.eventmanager.events.registrations.RegistrationEntity;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table
public class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "ownerId")
    private Long ownerId;
    @Column(name = "maxPlaces")
    private Integer maxPlaces;
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RegistrationEntity> registrationList;
    @Column(name = "date")
    private LocalDateTime date;
    @Column(name = "cost")
    private Integer cost;
    @Column(name = "duration")
    private Integer duration;
    @Column(name = "locationId")
    private Long locationId;
    @Column(name = "status")
    private String status;

    public EventEntity(Long id,
                       String name,
                       Long ownerId,
                       Integer maxPlaces,
                       List<RegistrationEntity> registrationList,
                       LocalDateTime date,
                       Integer cost,
                       Integer duration,
                       Long locationId,
                       String status) {
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
        this.maxPlaces = maxPlaces;
        this.registrationList = registrationList;
        this.date = date;
        this.cost = cost;
        this.duration = duration;
        this.locationId = locationId;
        this.status = status;
    }

    public EventEntity() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Integer getMaxPlaces() {
        return maxPlaces;
    }

    public void setMaxPlaces(Integer maxPlaces) {
        this.maxPlaces = maxPlaces;
    }

    public List<RegistrationEntity> getRegistrationList() {
        return registrationList;
    }

    public void setRegistrationList(List<RegistrationEntity> registrationList) {
        this.registrationList = registrationList;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
