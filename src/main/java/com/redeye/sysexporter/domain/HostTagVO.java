package com.redeye.sysexporter.domain;

import lombok.Data;

/**
 * 호스트 정보 Value Object
 *
 * @author jmsohn
 */
@Data
public class HostTagVO {
	
	/** 기관 코드 */
	private String organCode;
	
	/** 도메인 코드 */
	private String domainCode;
	
	/** 호스트 명 */
	private String hostName;
	
	/**
	 * 생성자
	 * 
	 * @param organCode 기관 코드
	 * @param domainCode 도메인 코드
	 * @param hostName 호스트 명
	 */
	public HostTagVO(
		String organCode,
		String domainCode,
		String hostName
	) {
		
		this.organCode = organCode;
		this.domainCode = domainCode;
		this.hostName = hostName;
	}

	
	@Override
	public String toString() {
		return new StringBuilder()
			.append(this.getOrganCode())
			.append(":")
			.append(this.getDomainCode())
			.append(":")
			.append(this.getHostName())
			.toString();
	}
}
