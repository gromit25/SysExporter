package com.redeye.sysexporter.acquisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import oshi.hardware.HWDiskStore;

/**
 * 
 * 
 * @author jmsohn
 */
@Component
public class DiskMetricsAcquisitor extends Acquisitor {
	
	/** */
	private long preTimestamp = -1;
	
	/** */
	private Map<String, DiskTotalMetrics> diskMetricsMap = null;

	
	/**
	 * 
	 */
	record DiskTotalMetrics (
		String diskName,
		long readBytes,
		long writeBytes
	){};
	
	/**
	 * 
	 */
	record DiskRateMetrics (
		String diskName,
		double readRate,
		double writeRate
	){};

	
	@Override
	protected String getName() {
		return "disk metrics acquisitor";
	}

	@Override
	protected String acquireMetrics() throws Exception {
		
		// 1. Disk 성능 정보 수집을 위한 준비 작업
		long curTimestamp = System.currentTimeMillis();
		List<HWDiskStore> diskList = this.getSysInfo().getHardware().getDiskStores();
		
		if(this.preTimestamp < 0) {
			
			this.preTimestamp = curTimestamp;
			this.diskMetricsMap = new ConcurrentHashMap<>();
		}

		// 2. Disk 성능 정보 추출
		Map<String, Object> diskMetrics = new HashMap<>();
		diskMetrics.put("type", "disk");
			
		List<DiskRateMetrics> diskRateMetricsList = new ArrayList<>();
		
		// Disk 목록 별로 성능 정보 추출
		for(HWDiskStore disk : diskList) {
			
			// 최신 정보로 갱신
			disk.updateAttributes();
			
			// 이전 read/write byte 획득
			DiskTotalMetrics preMetrics = this.diskMetricsMap.get(disk.getName());
			if(preMetrics != null) {
				
				// read/write 초당 byte 수 계산
				double divider = (curTimestamp - this.preTimestamp)/1000;
				double readRate = (disk.getReadBytes() - preMetrics.readBytes())/divider;
				double writeRate = (disk.getWriteBytes() - preMetrics.writeBytes())/divider;
				
				diskRateMetricsList.add(
					new DiskRateMetrics(
						disk.getName(),
						readRate,
						writeRate
					)
				);
			}
			
			// 이전 read/write byte 저장
			this.diskMetricsMap.put(
				disk.getName(),
				new DiskTotalMetrics(
					disk.getName(),
					disk.getReadBytes(),
					disk.getWriteBytes()
				)
			);
		}
		
		// 이전 시간 저장
		this.preTimestamp = curTimestamp;

		// 최초 성능 수집시 성능 정보가 없기 때문에 
		// 데이터를 보내지 않기 위해 null 을 반환
		if(diskRateMetricsList.size() == 0) {
			return null;
		} else {
			diskMetrics.put("disk", diskRateMetricsList);
			return this.objMapper.writeValueAsString(diskMetrics);
		}
	}
}
