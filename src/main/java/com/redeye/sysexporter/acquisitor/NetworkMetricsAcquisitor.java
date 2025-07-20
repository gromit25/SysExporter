package com.redeye.sysexporter.acquisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import oshi.hardware.NetworkIF;

/**
 * 
 * 
 * @author jmsohn
 */
@Slf4j
@Component
public class NetworkMetricsAcquisitor extends Acquisitor {
	
	/**
	 * 
	 */
	record NetIFTotalMetrics (
		long timestamp,
		String networkIFName,
		long bytesRecv,
		long bytesSent
	){};
	
	/**
	 * 
	 */
	record NetIFRateMetrics (
		String name,
		double recvRate,
		double sentRate
	){};
	
	
	@Override
	protected String getName() {
		return "network metrics acuisitor";
	}

	@Override
	protected Map<String, Object> acquireMetrics() throws Exception {
		
		// 1. 데이터 수집을 위한 준비
		Map<String, NetIFTotalMetrics> preMetricsMap = new HashMap<>();
		List<NetIFRateMetrics> netIFRateMetricsList = new ArrayList<>();
		
		List<NetworkIF> netIFList = this.getSysInfo().getHardware().getNetworkIFs();
		
		// 2. 이전 Network IF 성능 정보 수집
		
		// Network IF 목록 별로 성능 정보 추출
		for(NetworkIF netIF : netIFList) {
			
			// 최신 정보로 갱신
			netIF.updateAttributes();
			
			// 이전 read/write byte 저장
			preMetricsMap.put(
				netIF.getName(),
				new NetIFTotalMetrics(
					netIF.getTimeStamp(),
					netIF.getName(),
					netIF.getBytesRecv(),
					netIF.getBytesSent()
				)
			);
		}
		
		// 3. 대기(1초)
		try {
	        Thread.sleep(1000);
		} catch(Exception ex) {
			log.error(getName() + " exception", ex);
		}

		// 4. 현재 Network IF 성능 정보 수집 및
		//    이전 수집 정보와의 차이를 이용해 속도 계산
		
		for(NetworkIF netIF : netIFList) {
			
			// 최신 정보로 갱신
			netIF.updateAttributes();
			
			// 이전 read/write byte 저장
			NetIFTotalMetrics preMetrics = preMetricsMap.get(netIF.getName());
			if(preMetrics == null) {
				continue;
			}
			
			// read/write 초당 byte 수 계산
			double divider = (netIF.getTimeStamp() - preMetrics.timestamp())/1000;
			double recvRate = (netIF.getBytesRecv() - preMetrics.bytesRecv())/divider;
			double sentRate = (netIF.getBytesSent() - preMetrics.bytesSent())/divider;
			
			netIFRateMetricsList.add(
				new NetIFRateMetrics(
					netIF.getName(),
					recvRate,
					sentRate
				)
			);
		}
		
		// 5. 메시지 객체 생성 및 반환(JSON)
		Map<String, Object> netIFMetricsMap = new HashMap<>();
		
		netIFMetricsMap.put("type", "network");
		netIFMetricsMap.put("interface", netIFRateMetricsList);
		
		return netIFMetricsMap;
	}
}
