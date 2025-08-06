package com.redeye.sysexporter.acquisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;

/**
 * 디스크 사용량 정보 수집기
 * 
 * @author jmsohn
 */
@Component
public class DiskUsageMetricsAcquisitor extends Acquisitor{

	@Override
	protected String getName() {
		return "disk-usage";
	}

	@Override
	protected Map<String, Object> acquireMetrics() throws Exception {

		// 디스크 별 정보 수집
		FileSystem fs = this.getSysInfo().getOperatingSystem().getFileSystem();
		List<OSFileStore> diskList = fs.getFileStores();
		
		List<Map<String, Object>> diskUsageList = new ArrayList<>();
		
		for (OSFileStore disk : diskList) {
			
			Map<String, Object> diskUsage = new HashMap<>();
			
			diskUsage.put("diskName", disk.getName());
			diskUsage.put("total", disk.getTotalSpace());
			diskUsage.put("free", disk.getUsableSpace());
			
			diskUsageList.add(diskUsage);
		}
		
		// 메시지 객체 생성 및 반환(JSON)
		Map<String, Object> diskMetricsMap = new HashMap<>();
		diskMetricsMap.put("disk", diskUsageList);
		
		return diskMetricsMap;
	}
}
