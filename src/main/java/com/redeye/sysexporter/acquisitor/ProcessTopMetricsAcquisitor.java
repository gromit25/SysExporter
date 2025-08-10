package com.redeye.sysexporter.acquisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import oshi.hardware.CentralProcessor;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;
import oshi.software.os.OperatingSystem.ProcessSorting;

/**
 * CPU 사용율 Top 프로세스의 성능 정보 수집
 *
 * @author jmsohn
 */
@Component
@ConditionalOnProperty
(
	value = "app.acquisitor.process-top"
)
public class ProcessTopMetricsAcquisitor extends Acquisitor {
	
	/** top 개수 */
	@Value("app.acquisitor.process.top")
	private int top;
	
	@Override
	protected String getName() {
		return "process-top";
	}

	@Override
	protected Map<String, Object> acquireMetrics() {

		// 1. 시스템 정보 획득
		OperatingSystem os = this.getSysInfo().getOperatingSystem();
		CentralProcessor processor = this.getSysInfo().getHardware().getProcessor();

		// 시스템 논리 코어 수
		int logicalCoreCount = processor.getLogicalProcessorCount();
	
		// CPU 사용률 기준으로 상위 프로세스 목록 획득
		List<OSProcess> topProcessList = os.getProcesses(null, ProcessSorting.CPU_DESC, this.top);

		// 2. Top 프로세스 성능 정보 목록
		List<Map<String, Object>> processMetricsList = new ArrayList<>();
		
		for(int index = 0; index < topProcessList.size(); index++) {

			OSProcess process = topProcessList.get(index);
			
			// 프로세스 성능 정보 생성 및 추가
			Map<String, Object> processMetrics = new HashMap<>();

			processMetrics.put("ranking", index);
			processMetrics.put("pid", process.getProcessID());
			processMetrics.put("name", process.getName());
			processMetrics.put("cpuUsage", 100 * (process.getProcessCpuLoadCumulative() / logicalCoreCount));
			processMetrics.put("memUsed", process.getResidentSetSize());
			processMetrics.put("diskReadSize", process.getBytesRead());
			processMetrics.put("diskWriteSize", process.getBytesWritten());

			processMetricsList.add(processMetrics);
		}
		
		// 3. 메시지 객체 생성 및 반환
		Map<String, Object> processMetricsMap = new HashMap<>();
		processMetricsMap.put("processes", processMetricsList);

		return processMetricsMap;
	}
}
