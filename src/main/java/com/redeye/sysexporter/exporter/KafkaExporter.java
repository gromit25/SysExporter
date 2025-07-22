package com.redeye.sysexporter.exporter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * 
 * 
 * @author jmsohn
 */
public class KafkaExporter extends Exporter {

	/** */
	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	@Override
	public void send(String message) throws Exception {
		this.kafkaTemplate.send("", message);
	}
}
