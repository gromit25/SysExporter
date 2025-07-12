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
	protected void acquireMetrics() {
		
		// Memory
		GlobalMemory memory = this.getSysInfo().getHardware().getMemory();

		System.out.println("Total memory: " + memory.getTotal());
		System.out.println("Available memory: " + memory.getAvailable());
	}
}
