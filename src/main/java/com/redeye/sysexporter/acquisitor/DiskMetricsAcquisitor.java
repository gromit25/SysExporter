package com.redeye.sysexporter.acquisitor;

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
	private Map<String, DiskMetrics> diskMetricsMap = null;
	
	/**
	 * 
	 */
	record DiskMetrics (
		long readBytes,
		long writeBytes
	){};

	@Override
	protected String acquireMetrics() {
		
		// Disk
		long curTimestamp = System.currentTimeMillis();
		List<HWDiskStore> diskList = this.getSysInfo().getHardware().getDiskStores();
		
		if(this.preTimestamp < 0) {
			
			this.preTimestamp = curTimestamp;
			this.diskMetricsMap = new ConcurrentHashMap<>();
			
			for(HWDiskStore disk : diskList) {

				// 최신 정보로 갱신
				disk.updateAttributes();

				this.diskMetricsMap.put(
					disk.getName(),
					new DiskMetrics(
						disk.getReadBytes(),
						disk.getWriteBytes()
					)
				);
			}
			
			return null;
			
		} else {
			
			StringBuilder diskMsg = new StringBuilder();
			diskMsg.append("\"type\": \"disk\", ");
			
			for(HWDiskStore disk : diskList) {
				
				// 이전 read/write 획득
				DiskMetrics preMetrics = this.diskMetricsMap.get(disk.getName());
				if(preMetrics == null) {
					continue;
				}

				// 최신 정보로 갱신
				disk.updateAttributes();

				double divider = (curTimestamp - this.preTimestamp)/1000;
				double readRate = (disk.getReadBytes() - preMetrics.readBytes())/divider;
				double writeRate = (disk.getWriteBytes() - preMetrics.writeBytes())/divider;
				
				diskMsg
					.append("\"" + disk.getName() + "\":")
					.append("{ \"readRate\":")
					.append(readRate)
					.append(", \"writeRate\":")
					.append(writeRate)
					.append("}");
				
				this.diskMetricsMap.put(
						disk.getName(),
						new DiskMetrics(
							disk.getReadBytes(),
							disk.getWriteBytes()
						)
					);
			}
			
			this.preTimestamp = curTimestamp;
		}
	}
}
