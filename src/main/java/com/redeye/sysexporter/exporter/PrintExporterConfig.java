package com.redeye.sysexporter.exporter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * kafka exporter 에서 사용할  kafkaTemplate 객체 생성 컴포넌트
 *
 * @author jmsohn
 */
@Configuration
@ConditionalOnProperty
(
	value = "app.exporter.type",
	havingValue = "PRINT"
)
public class PrintExporterConfig {

	/**
	 * kafka exporter 생성 후 반환
	 * 
	 * @return kafka exporter
	 */
	@Bean("exporter")
	Exporter printExporter() {
		return new PrintExporter();
	}
}
