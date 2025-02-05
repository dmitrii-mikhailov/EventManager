package org.mikhailov.dm.eventmanager.events.registrations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RegistrationRepository extends JpaRepository<RegistrationEntity, Long> {
    RegistrationEntity findByEventId(Long eventId);
}
