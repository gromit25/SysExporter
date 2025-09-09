package com.redeye.sysexporter.util;

import org.json.JSONObject;

import com.redeye.sysexporter.domain.HostTagVO;

/**
 * Sys Exporter Util 클래스
 *
 * @author jmsohn
 */
public class Util {

	/**
	 * 메시지 JSON 객체에서 호스트 정보 추출
	 *
	 * @param msgJSON 메시지 JSON 객체
	 * @return 호스트 정보
	 */
	public static HostTagVO getHostTag(JSONObject msgJSON) throws Exception {

		if(msgJSON == null) {
			throw new IllegalArgumentException("msgJSON is null.");
		}
    
		return new HostTagVO(
			msgJSON.getString("organCode"),
			msgJSON.getString("domainCode"),
			msgJSON.getString("hostName")
		);
	}
}
