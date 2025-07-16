package com.redeye.sysexporter.acquisitor;

import java.util.List;

import org.springframework.stereotype.Component;

import oshi.software.os.OSProcess;

@Component
public class ProcessMetricsAcquisitor extends Acquisitor {

	@Override
	protected void acquireMetrics() {
		
		List<OSProcess> procList = this.getSysInfo().getOperatingSystem().getProcesses();
		
		for(OSProcess proc: procList) {
			
			if(proc.getName().startsWith("java") == true) {
			
				StringBuilder procMetrics = new StringBuilder("");
				procMetrics.append(proc.getName());
				procMetrics.append(" ").append(proc.getProcessCpuLoadCumulative());
				procMetrics.append(" ").append(proc.getResidentSetSize()).append("byte");
				
				// argument 확인
//				for(String arg: proc.getArguments()) {
//					procMetrics.append(" ").append(arg);
//				}
				
				System.out.println(procMetrics.toString());
			}
		}
	}
}
