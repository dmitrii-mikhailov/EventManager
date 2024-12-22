package org.mikhailov.dm.eventmanager.events;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<EventEntity, Long> {

    @Modifying
    @Transactional
    @Query("update EventEntity e set e.status = :status where e.id = :id")
    void updateEventStatus(
            @Param("id") Long eventId,
            @Param("status") String status
    );

    List<EventEntity> getEventEntityByOwnerId(Long ownerId);

    @Query("""
    SELECT e FROM EventEntity e
    WHERE (:name IS NULL OR e.name = :name)
    AND (:placesMin IS NULL OR e.maxPlaces >= :placesMin)
    AND (:placesMax IS NULL OR e.maxPlaces <= :placesMax)
    AND (cast(:dateStartAfter as date) IS NULL OR e.date >= :dateStartAfter)
    AND (cast(:dateStartBefore as date) IS NULL OR e.date <= :dateStartBefore)
    AND (:costMin IS NULL OR e.cost >= :costMin)
    AND (:costMax IS NULL OR e.cost <= :costMax)
    AND (:durationMin IS NULL OR e.duration >= :durationMin)
    AND (:durationMax IS NULL OR e.duration <= :durationMax)
    AND (:locationId IS NULL OR e.locationId = :locationId)
    AND (:eventStatus IS NULL OR e.status = :eventStatus)
    """)
    List<EventEntity> searchEvents(
            String name,
            Long placesMin,
            Long placesMax,
            LocalDateTime dateStartAfter,
            LocalDateTime dateStartBefore,
            Long costMin,
            Long costMax,
            Long durationMin,
            Long durationMax,
            Long locationId,
            String eventStatus
    );

    @Query("""
    SELECT e FROM EventEntity e
    JOIN FETCH e.registrationList r
    WHERE r.userId = :userRegistrationId
    """)
    List<EventEntity> getEventsByUserRegistrationId(Long userRegistrationId);

    //пока что сделал так (добавляю 3 часа), потом планирую заменить часовой пояс в контейнере докер
    @Transactional
    @Modifying
    @Query(value = """
    UPDATE event_entity
    SET status = 'STARTED'
    WHERE date < current_timestamp;
""", nativeQuery = true)
    void startEvents();

    @Transactional
    @Modifying
    @Query(value = """
    UPDATE event_entity
    SET status = 'FINISHED'
    WHERE status = 'STARTED'
    AND current_timestamp > (date + interval '1 minute' * duration)
""", nativeQuery = true)
    void finishEvents();
}

// current_timestamp + interval '3 hour'