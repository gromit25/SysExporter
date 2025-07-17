package com.redeye.sysexporter.acquisitor;

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
	protected String acquireMetrics() {
		
		// Memory
		GlobalMemory memory = this.getSysInfo().getHardware().getMemory();
		
		return new StringBuilder()
			.append("\"type\": \"mem\"")
			.append("\"total\":")
			.append(memory.getTotal())
			.append("\"available\":")
			.append(memory.getAvailable())
			.toString();
	}
}
