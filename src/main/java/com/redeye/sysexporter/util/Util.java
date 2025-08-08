package com.redeye.sysexporter.util;

import org.json.JSONObject;

import com.redeye.sysexporter.domain.HostVO;

/**
 * Sys Exporter Util 클래스
 *
 * @author jmsohn
 */
public class Util {

	/**
	 * 메시지 맵 객체에서 호스트 정보 추출
	 *
	 * @param msgMap 메시지 맵 객체
	 * @return 호스트 정보
	 */
	public static HostVO getHostInfo(Map<String, Object> msgMap) throws Exception {

		if(msgMap == null) {
			throw new IllegalArgumentException("msgMap is null.");
		}
    
		return new HostVO(
			msgMap.getString("organ"),
			msgMap.getString("region"),
			msgMap.getString("host")
		);
	}
}
