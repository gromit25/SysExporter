package com.redeye.sysexporter.exporter;

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

/**
 * kafka exporter 에서 사용할  kafkaTemplate 객체 생성 컴포넌트
 *
 * @author jmsohn
 */
@Configuration
@ConditionalOnProperty
(
	value = "app.exporter.type",
	havingValue = "KAFKA"
)
public class KafkaExporterConfig {

	/**
	 * kafka exporter 생성 후 반환
  	 *
    	 * @return kafka exporter
      	 */
	@Bean("exporter")
	Exporter kafkaExporter() {
		return new KafkaExporter();
	}

	/**
 	 * kafka template 생성 후 반환
	 *
	 * @param producerFactory kafka producer factory
	 * @return kafka template 객체
	 */
	@Bean
	KafkaTemplate<String, String> kafkaTemplate(
		@Qualifier("producerFactory") ProducerFactory<String, String> producerFactory 
	) {
		return new KafkaTemplate<>(producerFactory);
	}

	/**
	 * kafka producer factory 생성
	 * 
	 * @return kafka producer factory 객체
	 */
	@Bean
	ProducerFactory<String, String> producerFactory(
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
}
