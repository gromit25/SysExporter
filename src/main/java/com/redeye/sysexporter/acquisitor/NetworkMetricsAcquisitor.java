package com.redeye.sysexporter.acquisitor;

import org.springframework.stereotype.Component;

import oshi.hardware.NetworkIF;

/**
 * 
 * 
 * @author jmsohn
 */
@Component
public class NetworkMetricsAcquisitor extends Acquisitor {

	@Override
	protected void acquireMetrics() {
		
		// Network
		for(NetworkIF net : this.getSysInfo().getHardware().getNetworkIFs()) {
			
			// 최신 상태 반영
		    net.updateAttributes();
		    
		    System.out.println("Interface: " + net.getName());
		    System.out.println("Bytes sent: " + net.getBytesSent());
		    System.out.println("Bytes received: " + net.getBytesRecv());
		}
	}
}
