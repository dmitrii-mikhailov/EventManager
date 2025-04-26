package org.mikhailov.dm.eventmanager.kafkaevent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class EventKafkaEventSender {
    private static final Logger log = LoggerFactory.getLogger(EventKafkaEventSender.class);
    private final KafkaTemplate<Long, EventChangeKafkaMessage> kafkaTemplate;

    public EventKafkaEventSender(KafkaTemplate<Long, EventChangeKafkaMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEventChange(EventChangeKafkaMessage msg) {
        log.info("Sending event change {}", msg);
        CompletableFuture<SendResult<Long, EventChangeKafkaMessage>> result
                = kafkaTemplate.send("event-change-topic", msg.getEventId(), msg);
        result.thenAccept(sendResult -> {
           log.info("Event change {} sent to {}", msg.getEventId(), sendResult.getRecordMetadata());
        });
    }
}
