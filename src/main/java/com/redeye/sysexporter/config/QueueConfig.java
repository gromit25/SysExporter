package com.redeye.sysexporter.config;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 
 * 
 * @author jmsohn
 */
@Configuration
public class QueueConfig {

	/**
	 * 
	 * @return
	 */
	@Bean("toExporterQueue")
	BlockingQueue<String> toExporterQueue() {
		return new LinkedBlockingQueue<>();
	}
}
