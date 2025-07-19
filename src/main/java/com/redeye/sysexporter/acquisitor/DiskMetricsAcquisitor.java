package com.redeye.sysexporter.acquisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import oshi.hardware.HWDiskStore;

/**
 * 
 * 
 * @author jmsohn
 */
@Slf4j
@Component
public class DiskMetricsAcquisitor extends Acquisitor {
	
	/**
	 * 
	 */
	record DiskTotalMetrics (
		long timestamp,
		String diskName,
		long readBytes,
		long writeBytes
	){};
	
	/**
	 * 
	 */
	record DiskRateMetrics (
		String name,
		double readRate,
		double writeRate
	){};

	
	@Override
	protected String getName() {
		return "disk metrics acquisitor";
	}

	@Override
	protected String acquireMetrics() throws Exception {
		
		// 1. 데이터 수집을 위한 준비
		Map<String, DiskTotalMetrics> preMetricsMap = new HashMap<>();
		List<DiskRateMetrics> diskRateMetricsList = new ArrayList<>();
		
		List<HWDiskStore> diskList = this.getSysInfo().getHardware().getDiskStores();
		
		// 2. 이전 Disk 성능 정보 수집
		
		// Disk 목록 별로 성능 정보 추출
		for(HWDiskStore disk : diskList) {
			
			// 최신 정보로 갱신
			disk.updateAttributes();
			
			// 이전 read/write byte 저장
			preMetricsMap.put(
				disk.getName(),
				new DiskTotalMetrics(
					disk.getTimeStamp(),
					disk.getName(),
					disk.getReadBytes(),
					disk.getWriteBytes()
				)
			);
		}
		
		// 3. 대기(1초)
		try {
	        Thread.sleep(1000);
		} catch(Exception ex) {
			log.error(getName() + " exception", ex);
		}

		// 4. 현재 Disk 성능 정보 수집 및
		//    이전 수집 정보와의 차이를 이용해 속도 계산
		
		for(HWDiskStore disk : diskList) {
			
			// 최신 정보로 갱신
			disk.updateAttributes();
			
			// 이전 read/write byte 저장
			DiskTotalMetrics preMetrics = preMetricsMap.get(disk.getName());
			if(preMetrics == null) {
				continue;
			}
			
			// read/write 초당 byte 수 계산
			double divider = (disk.getTimeStamp() - preMetrics.timestamp())/1000;
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
		
		// 5. 메시지 객체 생성 및 반환(JSON)
		Map<String, Object> diskMetricsMap = new HashMap<>();
		
		diskMetricsMap.put("type", "disk");
		diskMetricsMap.put("disk", diskRateMetricsList);
		
		return this.toJSON(diskMetricsMap);
	}
}
