package com.redeye.sysexporter.acquisitor;

import lombok.Getter;

/**
 * 성능 지표 타입
 * 
 * @author jmsohn
 */
public enum MetricsType {

	CPU("cpu"),
	MEMORY("memory"),
	DISK("disk"),
	NETWORK("network"),
	TOP_PROCESS("top-process"),
	TARGET_PROCESS("target-process");
	
	// ---------
	
	/** 타입 명 */
	@Getter
	private String typeName;
	
	/**
	 * 생성자
	 * 
	 * @param typeName 타입 명
	 */
	MetricsType(String typeName) {
		this.typeName = typeName;
	}
}
