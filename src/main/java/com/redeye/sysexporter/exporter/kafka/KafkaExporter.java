package com.redeye.sysexporter.exporter.kafka;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.jutools.StringUtil;
import com.redeye.sysexporter.domain.HostTagVO;
import com.redeye.sysexporter.exporter.Exporter;
import com.redeye.sysexporter.util.Util;

import lombok.extern.slf4j.Slf4j;

/**
 * Kafka Exporter
 * 
 * @author jmsohn
 */
@Slf4j
@Component("exporter")
@ConditionalOnProperty
(
	value = "app.exporter.type",
	havingValue = "KAFKA"
)
public class KafkaExporter extends Exporter {

	/** kafka 전송 객체 */
	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;
	
	/** kafka 전송 토픽 */
	@Value("${app.exporter.kafka.topic}")
	private String topic;
	
	
	@Override
	public void send(String message) throws Exception {
		
		if(StringUtil.isBlank(message) == true) {
			return;
		}
		
		String key = getKey(message);
		log.info("SEND KAFKA: " + key);
		
		this.kafkaTemplate.send(this.topic, key, message);
	}
	
	/**
	 * kafka 전송시 사용할 key를 생성하여 반환함
	 * 
	 * @param message 전송할 메시지
	 * @return 생성된 key
	 */
	private static String getKey(String message) throws Exception {
		
		JSONObject messageJSON = new JSONObject(message);

		HostTagVO hostTag = Util.getHostInfo(messageJSON);

		// sys-exporter:host:type
		return new StringBuilder()
			.append("sys-exporter:")
			.append(hostTag)
			.append(":")
			.append(messageJSON.getString("type"))
			.toString();
	}
}
