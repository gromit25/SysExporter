package com.redeye.sysexporter.acquisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import oshi.hardware.NetworkIF;

/**
 * 
 * 
 * @author jmsohn
 */
@Component
public class NetworkMetricsAcquisitor extends Acquisitor {
	
	/** */
	private long preTimestamp = -1;
	
	/** */
	private Map<String, NetworkIFTotalMetrics> networkIFMetricsMap = null;
	
	
	/**
	 * 
	 */
	record NetworkIFTotalMetrics (
		String networkIFName,
		long bytesRecv,
		long bytesSent
	){};
	
	/**
	 * 
	 */
	record NetworkIFRateMetrics (
		String networkIFName,
		double recvRate,
		double sentRate
	){};
	
	
	@Override
	protected String getName() {
		return "network metrics acuisitor";
	}

	@Override
	protected String acquireMetrics() throws Exception {
		
		// 1. Network IF 성능 정보 수집을 위한 준비 작업
		long curTimestamp = System.currentTimeMillis();
		List<NetworkIF> networkIFList = this.getSysInfo().getHardware().getNetworkIFs();
		
		if(this.preTimestamp < 0) {
			
			this.preTimestamp = curTimestamp;
			this.networkIFMetricsMap = new ConcurrentHashMap<>();
		}

		// 2. Network IF 성능 정보 추출
		Map<String, Object> networkIFMetrics = new HashMap<>();
		networkIFMetrics.put("type", "network");
			
		List<NetworkIFRateMetrics> networkIFRateMetricsList = new ArrayList<>();
		
		// Network IF 목록 별로 성능 정보 추출
		for(NetworkIF networkIF : networkIFList) {
			
			// 최신 정보로 갱신
			networkIF.updateAttributes();
			
			// 이전 recv/sent byte 획득
			NetworkIFTotalMetrics preMetrics = this.networkIFMetricsMap.get(networkIF.getName());
			if(preMetrics != null) {
				
				// recv/sent 초당 byte 수 계산
				double divider = (curTimestamp - this.preTimestamp)/1000;
				double readRate = (networkIF.getBytesRecv() - preMetrics.bytesRecv())/divider;
				double writeRate = (networkIF.getBytesSent() - preMetrics.bytesSent())/divider;
				
				networkIFRateMetricsList.add(
					new NetworkIFRateMetrics(
						networkIF.getName(),
						readRate,
						writeRate
					)
				);
			}
			
			// 이전 read/write byte 저장
			this.networkIFMetricsMap.put(
				networkIF.getName(),
				new NetworkIFTotalMetrics(
					networkIF.getName(),
					networkIF.getBytesRecv(),
					networkIF.getBytesSent()
				)
			);
		}
		
		// 이전 시간 저장
		this.preTimestamp = curTimestamp;

		// 최초 성능 수집시 성능 정보가 없기 때문에 
		// 데이터를 보내지 않기 위해 null 을 반환
		if(networkIFRateMetricsList.size() == 0) {
			return null;
		} else {
			networkIFMetrics.put("network", networkIFRateMetricsList);
			return this.objMapper.writeValueAsString(networkIFMetrics);
		}
	}
}
