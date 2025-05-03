package org.mikhailov.dm.eventmanager.events;

import org.mikhailov.dm.eventmanager.kafkaevent.EventChangeKafkaMessage;
import org.mikhailov.dm.eventmanager.kafkaevent.EventFieldChange;
import org.mikhailov.dm.eventmanager.kafkaevent.EventKafkaEventSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EventScheduleService {

    private final EventRepository eventRepository;
    private static final Logger log = LoggerFactory.getLogger(EventScheduleService.class);
    private final EventKafkaEventSender eventKafkaEventSender;

    public EventScheduleService(EventRepository eventRepository, EventKafkaEventSender kafkaEventSender) {
        this.eventRepository = eventRepository;
        this.eventKafkaEventSender = kafkaEventSender;
    }

    @Scheduled(fixedRate = 1000*1000)
    public void triggerEventSchedule() {
        log.info("Starting time-appropriate events that are in status {}", EventStatus.WAIT_START);

        List<EventEntity> eventsToStart = eventRepository.findByStatus(EventStatus.WAIT_START.name());

        eventRepository.startEvents();

        eventsToStart.forEach(event -> {
            eventKafkaEventSender.sendEventChange(new EventChangeKafkaMessage(
                    event.getId(),
                    null,
                    event.getOwnerId(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    new EventFieldChange<>(EventStatus.WAIT_START.name(), EventStatus.STARTED.name()),
                    null
            ));
        });

        log.info("Finishing time-appropriate events that are in status {}", EventStatus.STARTED);

        List<EventEntity> eventsToFinish = eventRepository.findByStatus(EventStatus.STARTED.name());
        eventRepository.finishEvents();

        eventsToFinish.forEach(event -> {
            eventKafkaEventSender.sendEventChange(new EventChangeKafkaMessage(
                    event.getId(),
                    null,
                    event.getOwnerId(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    new EventFieldChange<>(EventStatus.STARTED.name(), EventStatus.FINISHED.name()),
                    null
            ));
        });
    }
}