package com.redeye.sysexporter.acquisitor;

import java.util.List;

import org.springframework.stereotype.Component;

import oshi.software.os.OSProcess;

@Component
public class ProcessMetricsAcquisitor extends Acquisitor {

	@Override
	protected String acquireMetrics() {
		
		List<OSProcess> procList = this.getSysInfo().getOperatingSystem().getProcesses();
		
		for(OSProcess proc: procList) {
			
			if(proc.getName().startsWith("java") == true) {
			
				StringBuilder procMetrics = new StringBuilder()
					.append(proc.getName())
					.append("\t")
					.append(proc.getProcessCpuLoadCumulative())
					.append("\t")
					.append(proc.getResidentSetSize())
					.append("byte");
				
				// argument 확인
//				for(String arg: proc.getArguments()) {
//					procMetrics.append(" ").append(arg);
//				}
				
				System.out.println(procMetrics.toString());
			}
		}
	}
}
