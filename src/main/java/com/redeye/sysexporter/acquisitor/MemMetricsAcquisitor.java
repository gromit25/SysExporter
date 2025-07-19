package com.redeye.sysexporter.acquisitor;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import oshi.hardware.GlobalMemory;

/**
 * 
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
	protected String acquireMetrics() throws Exception {
		
		// Memory
		GlobalMemory memory = this.getSysInfo().getHardware().getMemory();
		
		Map<String, Object> memMetrics = new HashMap<>();
		memMetrics.put("type", "memory");
		memMetrics.put("total", memory.getTotal());
		memMetrics.put("available", memory.getAvailable());
		
		return this.objMapper.writeValueAsString(memMetrics);
	}
}
