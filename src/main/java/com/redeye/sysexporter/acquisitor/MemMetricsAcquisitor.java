package com.redeye.sysexporter.acquisitor;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import oshi.hardware.GlobalMemory;

/**
 * 메모리 성능 정보 수집기
 * 
 * @author jmsohn
 */
@Component
public class MemMetricsAcquisitor extends Acquisitor{
	
	@Override
	protected String getName() {
		return "memory metrics acquisitor";
	}

	@Override
	protected Map<String, Object> acquireMetrics() throws Exception {
		
		// Memory 정보 수집
		GlobalMemory memory = this.getSysInfo().getHardware().getMemory();
		
		// 메시지 객체 생성 및 반환(JSON)
		Map<String, Object> memMetrics = new HashMap<>();
		memMetrics.put("type", "memory");
		memMetrics.put("total", memory.getTotal());
		memMetrics.put("available", memory.getAvailable());
		
		return memMetrics;
	}
}
