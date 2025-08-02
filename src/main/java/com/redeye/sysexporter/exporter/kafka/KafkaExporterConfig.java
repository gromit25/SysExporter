package com.redeye.sysexporter.exporter.kafka;

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
 * kafka exporter 및 kafkaTemplate 객체 생성 컴포넌트
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
	 * @param host 접속할 kafka 호스트 명
	 * @return kafka producer factory 객체
	 */
	@Bean
	ProducerFactory<String, String> producerFactory(
		@Value("${app.exporter.kafka.servers}") String servers,
		@Value("${app.exporter.kafka.acks}") String acks
	) {
		
		// 입력값 검증
		if(StringUtil.isBlank(servers) == true) {
			throw new IllegalArgumentException("app.exporter.kafka.servers is null or blank.");
		}
		
		if(StringUtil.isBlank(acks) == true) {
			throw new IllegalArgumentException("app.exporter.kafka.acks is null or blank.");
		}

		Map<String, Object> configProps = new HashMap<>();

		// 연결 설정
		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		configProps.put(ProducerConfig.ACKS_CONFIG, acks);

		return new DefaultKafkaProducerFactory<>(configProps);
	}
}
