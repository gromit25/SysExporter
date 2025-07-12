package com.redeye.sysexporter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.redeye.sysexporter.acquisitor.CPUMetricsAcquisitor;
import com.redeye.sysexporter.acquisitor.DiskMetricsAcquisitor;
import com.redeye.sysexporter.acquisitor.MemMetricsAcquisitor;
import com.redeye.sysexporter.acquisitor.NetworkMetricsAcquisitor;

/**
 * 
 * 
 * @author jmsohn
 */
@SpringBootApplication
public class SysExporterApplication implements CommandLineRunner {
	
	@Autowired
	private CPUMetricsAcquisitor cpuAcquisitor;
	
	@Autowired
	private MemMetricsAcquisitor memAcquisitor;
	
	@Autowired
	private NetworkMetricsAcquisitor netAcquisitor;
	
	@Autowired
	private DiskMetricsAcquisitor diskAcquisitor;
	
	/**
	 * 
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		SpringApplication.run(SysExporterApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		
		System.out.println("start");
		
		//this.cpuAcquisitor.start();
		//this.memAcquisitor.start();
		this.diskAcquisitor.start();
		//this.netAcquisitor.start();
		
		Thread.sleep(1000 * 60 * 60);
		
		System.out.println("end");
	}
}
