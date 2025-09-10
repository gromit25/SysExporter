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

	/** RestAPI Subpath */
	private static final String SUBPATH = "/api/sysmetrics/%s/%s/%s";

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
	 * @param msgJSON 발송할 메시지
  	 * @return 타입 별 subpath
	 */
	private static String getSubpath(JSONObject msgJSON) throws Exception {
		
		String type = msgJSON.getString("type");
		HostTagVO hostTag = Util.getHostTag(msgJSON);

		return String.format(SUBPATH, hostTag.getOrganCode(), hostTag.getDomainCode(), hostTag.getHostName());
	}
}
