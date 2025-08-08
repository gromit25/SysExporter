package com.redeye.sysexporter.util;

import com.redeye.sysexporter.domain.HostVO;

/**
 * Sys Exporter Util 클래스
 *
 * @author jmsohn
 */
public class Util {

  /**
   * 메시지 JSON 객체에서 호스트 정보 추출
   *
   * @param messageJSON 메시지 JSON 객체
   * @return 호스트 정보
   */
  public static HostVO getHostInfo(JSONObject messageJSON) throws Exception {

    if(messageJSON == null) {
      throw new IllegalArgumentException("messageJSON is null.");
    }
    
    return new HostVO(
      messageJSON.getString("organ"),
      messageJSON.getString("region"),
      messageJSON.getString("host")
    );
  }
}
