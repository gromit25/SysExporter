package com.redeye.sysexporter;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.jutools.FileUtil;
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
	
	@Value("${app.stop.file}")
	private File stopFile;
	
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
		
		// 중단 파일이 touch 될때까지 대기
		FileUtil.waitForFileTouched(stopFile);
		
		// 정보 수집 중단
		this.diskAcquisitor.stop();
		
		System.out.println("end");
	}
}
