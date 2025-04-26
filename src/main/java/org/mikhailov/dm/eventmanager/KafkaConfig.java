package org.mikhailov.dm.eventmanager;

import org.mikhailov.dm.eventmanager.kafkaevent.EventChangeKafkaMessage;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.ssl.DefaultSslBundleRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;

@Configuration
public class KafkaConfig {
    @Bean
    public KafkaTemplate<Long, EventChangeKafkaMessage> kafkaTemplate(
            KafkaProperties kafkaProperties
    ) {
        Map<String, Object> props = kafkaProperties.buildProducerProperties(
                new DefaultSslBundleRegistry()
        );

        ProducerFactory<Long, EventChangeKafkaMessage> pf = new DefaultKafkaProducerFactory<>(props);
        return new KafkaTemplate<>(pf);
    }
}
