package com.redeye.sysexporter.exporter.print;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.redeye.sysexporter.exporter.Exporter;

/**
 * print exporter 생성 컴포넌트
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
	 * print exporter 생성 후 반환
	 * 
	 * @return print exporter
	 */
	@Bean("exporter")
	Exporter printExporter() {
		return new PrintExporter();
	}
}
