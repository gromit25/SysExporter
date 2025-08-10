package com.redeye.sysexporter.acquisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.jutools.StringUtil;

import jakarta.annotation.PostConstruct;
import oshi.hardware.CentralProcessor;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

/**
 * 
 * 
 * @author jmsohn
 */
@Component
@ConditionalOnProperty
(
	value = "app.acquisitor.process.filter"
)
public class ProcessFilterAqcuisitor extends Acquisitor {
	
	/** */
	private Set<String> processPatternSet = ConcurrentHashMap.newKeySet();
	
	/**
	 * 
	 */
	@PostConstruct
	public void initAcquisitor() throws Exception {
		
		String processPatternListStr = "java Test.jar, java eclipse.jar";
		String[] processPatternAry = processPatternListStr.split("[ \\t]*,[ \\t]*");
		
		for(String processPattern: processPatternAry) {
			this.processPatternSet.add(processPattern);
		}
	}
	
	@Override
	protected String getName() {
		return "process-filter";
	}

	@Override
	protected Map<String, Object> acquireMetrics() throws Exception {
		
		// 1. 시스템 정보 획득
		OperatingSystem os = this.getSysInfo().getOperatingSystem();
		CentralProcessor processor = this.getSysInfo().getHardware().getProcessor();
		
		// 시스템 논리 코어 수
		int logicalCoreCount = processor.getLogicalProcessorCount();
		
		// 2. Top 프로세스 성능 정보 목록
		List<Map<String, Object>> processMetricsList = new ArrayList<>();

		List<OSProcess> processList = os.getProcesses();
		
		for(OSProcess process: processList) {
			
			if(this.isMatch(process) == true) {
				
				// 프로세스 성능 정보 생성 및 추가
				Map<String, Object> processMetrics = new HashMap<>();
				
				processMetrics.put("pid", process.getProcessID());
				processMetrics.put("name", process.getName());
				processMetrics.put("cpuUsage", 100 * (process.getProcessCpuLoadCumulative() / logicalCoreCount));
				processMetrics.put("memUsed", process.getResidentSetSize());
				processMetrics.put("diskReadSize", process.getBytesRead());
				processMetrics.put("diskWriteSize", process.getBytesWritten());
	
				processMetricsList.add(processMetrics);
			}
		}
		
		// 3. 메시지 객체 생성 및 반환
		Map<String, Object> processMetricsMap = new HashMap<>();
		processMetricsMap.put("processes", processMetricsList);

		return processMetricsMap;
	}
	
	/**
	 * 
	 * 
	 * @param p
	 * @return
	 */
	private boolean isMatch(OSProcess p) throws Exception {
		
		String processName = p.getName();
		
		for(String processPattern : this.processPatternSet) {
			
			String[] processPatternAry = StringUtil.splitFirst(processPattern, "[ \t]+");
			if(StringUtil.matchWildcard(processPatternAry[0], processName) == true) {
				
				// Argument에 대한 조건이 없는 경우에는 true를 반환
				if(processPatternAry.length == 1) {
					return true;
				}
				
				// Argument 생성
				StringBuilder args = new StringBuilder();
				
				List<String> argList = p.getArguments();
				for(String arg: argList) {
					args.append(" ").append(arg);
				}
				
				//
				if(StringUtil.matchWildcard(processPattern, args.toString()) == true) {
					return true;
				}
			}
		}
		
		return false;
	}
}
