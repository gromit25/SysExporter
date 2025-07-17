package com.redeye.sysexporter.acquisitor;

import org.springframework.stereotype.Component;

import oshi.hardware.CentralProcessor;

/**
 * 
 * 
 * @author jmsohn
 */
@Component
public class CPUMetricsAcquisitor extends Acquisitor {
	
	/**
	 * 
	 */
	protected String acquireMetrics() {
		
		// CPU
		CentralProcessor cpu = this.getSysInfo().getHardware().getProcessor();

		long[][] prevTickArr = cpu.getProcessorCpuLoadTicks();
		try {
	        // 대기 시간 (예: 1초)
	        Thread.sleep(1000);
		} catch(Exception ex) {
			// Do nothing
		}

		// CPU별 사용률 계산
		double[] loadArr = cpu.getProcessorCpuLoadBetweenTicks(prevTickArr);

		// 전체 평균 사용률 계산
		double totalLoad = 0;
		for (double load : loadArr) {
			totalLoad += load;
		}
		
		double avgLoad = (totalLoad / loadArr.length) * 100;
		
		return new StringBuilder()
			.append("{ \"type\": \"cpu\", ")
			.append("\"usage\":")
			.append(avgLoad)
			.append("}")
			.toString();
	}
}
