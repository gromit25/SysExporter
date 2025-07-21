package com.redeye.sysexporter.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import com.jutools.StringUtil;

@Configuration
@ConditionalOnProperty
(
	value = "app.exporter.type",
	havingValue = "KAFKA"
)
public class KafkaExporterConfig {
	
	@Bean
	public ProducerFactory<String, String> producerFactory(
		@Value("${app.exporter.kafka.host}") String host
	) {
		
		if(StringUtil.isBlank(host) == true) {
			throw new IllegalArgumentException("app.exporter.kafka.host is null or blank.");
		}

        Map<String, Object> configProps = new HashMap<>();

        // 연결 설정
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, host);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        return new DefaultKafkaProducerFactory<>(configProps);
    }
	
	@Bean
	public KafkaTemplate<String, String> kafkaTemplate(
		@Qualifier("producerFactory") ProducerFactory<String, String> producerFactory 
	) {
		return new KafkaTemplate<>(producerFactory);
	}
}
