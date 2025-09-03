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
	public static HostTagVO getHostInfo(JSONObject msgJSON) throws Exception {

		if(msgJSON == null) {
<<<<<<< HEAD
			throw new IllegalArgumentException("msgJSON is null.");
=======
			throw new IllegalArgumentException("messageJSON is null.");
>>>>>>> branch 'main' of https://github.com/gromit25/SysExporter.git
		}
    
		return new HostTagVO(
			msgJSON.getString("organCode"),
			msgJSON.getString("domainCode"),
			msgJSON.getString("hostName")
		);
	}
}
