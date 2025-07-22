package com.redeye.sysexporter;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * acquisitor -> exporter 전달용 큐 생성 컴포넌트
 * 
 * @author jmsohn
 */
@Configuration
public class QueueConfig {

	/**
	 * 큐 생성 후 반환
  	 * 
	 * @return 생성된 큐
	 */
	@Bean("toExporterQueue")
	BlockingQueue<String> toExporterQueue() {
		return new LinkedBlockingQueue<>();
	}
}
