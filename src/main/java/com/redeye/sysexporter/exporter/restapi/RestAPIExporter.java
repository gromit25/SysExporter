package com.redeye.sysexporter.exporter.restapi;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.jutools.StringUtil;
import com.redeye.sysexporter.domain.HostTagVO;
import com.redeye.sysexporter.exporter.Exporter;
import com.redeye.sysexporter.util.Util;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * Rest API Exporter
 *
 * @author jmsohn
 */
@Slf4j
@Component("exporter")
@ConditionalOnProperty
(
	value = "app.exporter.type",
	havingValue = "RESTAPI"
)
public class RestAPIExporter extends Exporter {

	/** cpu 사용율 api subpath */
	private static final String SUBPATH_CPU = "/host/%s/%s/metrics/cpu";
	
	/** memory 사용율 api subpath */
	private static final String SUBPATH_MEM = "/host/%s/%s/metrics/memory";
	
	/** disk 사용율 api subpath */
	private static final String SUBPATH_DISK = "/host/%s/%s/metrics/disk";
	
	/** network io 사용율 api subpath */
	private static final String SUBPATH_NETWORK = "/host/%s/%s/metrics/network";
	
	/** process top 5 api subpath */
	private static final String SUBPATH_PROCESS_TOP = "/host/%s/%s/metrics/process/top";

	/** rest api 연결 클라이언트 */
	@Autowired
	@Qualifier("webClient")
	private WebClient client;

	
	@Override
	public void send(String message) throws Exception {

		// message가 null 이거나 blank 일 경우 반환
		if(StringUtil.isBlank(message) == true) {
			return;
		}

		// JSON 메시지로 변환
		JSONObject messageJSON = new JSONObject(message);

		// 호출할 Subpath 획득
		String subpath = getSubpath(messageJSON);

		// api 호출
		client.post()
			.uri(subpath)
			.bodyValue(messageJSON)
			.retrieve()
			.onStatus(HttpStatusCode::isError, response -> {
				
				// 오류 로깅
				log.error("{}\t{}", response.statusCode(), subpath);
                    
				// 예외를 발생시켜 Mono 체인에 에러를 전파
				return response.createException().flatMap(throwable -> Mono.error(throwable));
			})
			.bodyToMono(Void.class);
	}

	/**
	 * 메시지의 타입 별 subpath를 반환
	 *
	 * @param messageJSON 발송할 메시지
  	 * @return 타입 별 subpath
	 */
	private static String getSubpath(JSONObject messageJSON) throws Exception {
		
		String type = messageJSON.getString("type");

		HostTagVO hostTag = Util.getHostInfo(messageJSON);
		
		return
		switch(type) {
			case "cpu" -> String.format(SUBPATH_CPU, hostTag.domainCode(), hostTag.hostName());
			case "memory" -> String.format(SUBPATH_MEM, hostTag.domainCode(), hostTag.hostName());
			case "disk-usage" -> String.format(SUBPATH_DISK, hostTag.domainCode(), hostTag.hostName());
			case "network-io" -> String.format(SUBPATH_NETWORK, hostTag.domainCode(), hostTag.hostName());
			case "process-top" -> String.format(SUBPATH_PROCESS_TOP, hostTag.domainCode(), hostTag.hostName());
			default -> throw new Exception("unknown type:" + type);
		};
	}
}
