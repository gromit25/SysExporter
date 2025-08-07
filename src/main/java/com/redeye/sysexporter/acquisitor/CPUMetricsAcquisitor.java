package com.redeye.sysexporter.acquisitor;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import oshi.hardware.CentralProcessor;

/**
 * CPU 성능 정보 수집기
 * 
 * @author jmsohn
 */
@Slf4j
@Component
@ConditionalOnProperty
(
	value = "app.aquisitor.cpu",
	havingValue = "y"
)
public class CPUMetricsAcquisitor extends Acquisitor {

	@Override
	protected String getName() {
		return "cpu";
	}

	@Override
	protected Map<String, Object> acquireMetrics() throws Exception {
		
		// 1. 데이터 수집을 위한 준비
		CentralProcessor cpu = this.getSysInfo().getHardware().getProcessor();

		// 2. 이전 CPU 성능 정보 수집
		long preTime = System.currentTimeMillis();
		long[][] prevTickArr = cpu.getProcessorCpuLoadTicks();
		
		// 3. 대기(1초)
		try {
	        Thread.sleep(1000);
		} catch(Exception ex) {
			log.error(getName() + " exception", ex);
		}

		// 4. 현재 CPU 성능 정보 수집 및
		//    이전 수집 정보와의 차이를 이용해 이용율 계산
		long curTime = System.currentTimeMillis();
		double[] usageArr = cpu.getProcessorCpuLoadBetweenTicks(prevTickArr);

		// 전체 평균 사용률 계산
		double totalUsage = 0;
		for (double usage : usageArr) {
			totalUsage += usage;
		}
		
		// 초당 평균 사용율
		double avgLoad = ((totalUsage / usageArr.length) * 100) / ((curTime - preTime)/1000);
		
		// 5. 메시지 객체 생성 및 반환(JSON)
		Map<String, Object> cpuMetricsMap = new HashMap<>();
		cpuMetricsMap.put("usage", avgLoad);
		
		return cpuMetricsMap;
	}
}
