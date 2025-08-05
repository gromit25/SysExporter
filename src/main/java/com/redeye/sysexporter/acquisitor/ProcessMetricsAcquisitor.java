package com.redeye.sysexporter.acquisitor;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import oshi.software.os.OSProcess;

/**
 * CPU 사용율 Top 5 프로세스의 성능 정보 수집
 *
 * @author jmsohn
 */
@Component
public class ProcessMetricsAcquisitor extends Acquisitor {
	
	@Override
	protected String getName() {
		return "process";
	}

	@Override
	protected Map<String, Object> acquireMetrics() {

		// 시스템 정보 획득
		OperatingSystem os = this.getSysInfo().getOperatingSystem();
		CentralProcessor processor = this.getSysInfo().getHardware().getProcessor();

		// 시스템 논리 코어 수
		int logicalCoreCount = processor.getLogicalProcessorCount();
	
		// CPU 사용률 기준으로 상위 5개 프로세스 목록 획득
		List<OSProcess> top5ProcesseList = os.getProcesses(null, ProcessSorting.CPU_DESC, 5);

		// Top 5 프로세스 성능 정보 목록
		List<Map<String, Object>> processMetricsList = new ArrayList<>();
		
		for(int index = 0; index < top5ProcesseList.size(); index++) {

			OSProcess process = top5ProcesseList.get(index);
			
			// 프로세스 성능 정보 생성
			Map<String, Object> processMetrics = new HashMap<>();

			processMetrics.put("order", index);
			processMetrics.put("pid", process.getProcessID());
			processMetrics.put("name", process.getName());
			processMetrics.put("cpuUsage", 100d * (process.getProcessCpuLoadCumulative() / logicalCoreCount));
			processMetrics.put("memUsed", process.getResidentSetSize());
			processMetrics.put("diskReadSize", process.getBytesRead());
			processMetrics.put("diskWriteSize", process.getBytesWritten());

			processMetricsList.add(processMetrics);
		}

		return processMetricsList;
	}
}
