package org.mikhailov.dm.eventmanager.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EventScheduleService {

    private final EventRepository eventRepository;
    private static final Logger log = LoggerFactory.getLogger(EventScheduleService.class);

    public EventScheduleService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Scheduled(fixedRate = 1000*10)
    public void triggerEventSchedule() {
        log.info("Starting time-appropriate events that are in status {}", EventStatus.WAIT_START);
        eventRepository.startEvents();

        log.info("Finishing time-appropriate events that are in status {}", EventStatus.STARTED);
        eventRepository.finishEvents();
    }
}