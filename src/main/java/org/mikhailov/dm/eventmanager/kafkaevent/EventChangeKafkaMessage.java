package org.mikhailov.dm.eventmanager.kafkaevent;

import org.springframework.messaging.handler.annotation.Payload;

import java.time.LocalDateTime;
import java.util.List;

public class EventChangeKafkaMessage {
    private Long eventId;
    private Long changedByUserId;
    private Long ownerId;
    private EventFieldChange<String> name;
    private EventFieldChange<Integer> maxPlaces;
    private EventFieldChange<LocalDateTime> date;
    private EventFieldChange<Integer> cost;
    private EventFieldChange<Integer> duration;
    private EventFieldChange<Long> locationId;
    private EventFieldChange<String> status;

    private List<Long> users;

    public EventChangeKafkaMessage(Long eventId,
                                   Long changedByUserId,
                                   Long ownerId,
                                   EventFieldChange<String> name,
                                   EventFieldChange<Integer> maxPlaces,
                                   EventFieldChange<LocalDateTime> date,
                                   EventFieldChange<Integer> cost,
                                   EventFieldChange<Integer> duration,
                                   EventFieldChange<Long> locationId,
                                   EventFieldChange<String> status,
                                   @Payload List<Long> users) {
        this.eventId = eventId;
        this.changedByUserId = changedByUserId;
        this.ownerId = ownerId;
        this.name = name;
        this.maxPlaces = maxPlaces;
        this.date = date;
        this.cost = cost;
        this.duration = duration;
        this.locationId = locationId;
        this.status = status;
        this.users = users;
    }

    public EventChangeKafkaMessage() {
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getChangedByUserId() {
        return changedByUserId;
    }

    public void setChangedByUserId(Long changedByUserId) {
        this.changedByUserId = changedByUserId;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public EventFieldChange<String> getName() {
        return name;
    }

    public void setName(EventFieldChange<String> name) {
        this.name = name;
    }

    public EventFieldChange<Integer> getMaxPlaces() {
        return maxPlaces;
    }

    public void setMaxPlaces(EventFieldChange<Integer> maxPlaces) {
        this.maxPlaces = maxPlaces;
    }

    public EventFieldChange<LocalDateTime> getDate() {
        return date;
    }

    public void setDate(EventFieldChange<LocalDateTime> date) {
        this.date = date;
    }

    public EventFieldChange<Integer> getCost() {
        return cost;
    }

    public void setCost(EventFieldChange<Integer> cost) {
        this.cost = cost;
    }

    public EventFieldChange<Integer> getDuration() {
        return duration;
    }

    public void setDuration(EventFieldChange<Integer> duration) {
        this.duration = duration;
    }

    public EventFieldChange<Long> getLocationId() {
        return locationId;
    }

    public void setLocationId(EventFieldChange<Long> locationId) {
        this.locationId = locationId;
    }

    public EventFieldChange<String> getStatus() {
        return status;
    }

    public void setStatus(EventFieldChange<String> status) {
        this.status = status;
    }

    public List<Long> getUsers() {
        return users;
    }

    public void setUsers(List<Long> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "EventChangeKafkaMessage{" +
                "eventId=" + eventId +
                ", changedByUserId=" + changedByUserId +
                ", ownerId=" + ownerId +
                ", name=" + name +
                ", maxPlaces=" + maxPlaces +
                ", date=" + date +
                ", cost=" + cost +
                ", duration=" + duration +
                ", locationId=" + locationId +
                ", status=" + status +
                ", users=" + users +
                '}';
    }
}

