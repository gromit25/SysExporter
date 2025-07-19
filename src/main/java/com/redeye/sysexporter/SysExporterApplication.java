package com.redeye.sysexporter;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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
import com.redeye.sysexporter.acquisitor.ProcessMetricsAcquisitor;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 
 * @author jmsohn
 */
@Slf4j
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
	
	@Autowired
	private ProcessMetricsAcquisitor procAcquisitor;
	
	/** */
	private BlockingQueue<String> toExporterQueue;
	
	
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
		
		// 준비
		log.info("prepare to start sys-collector");
		this.prepare();
		
		// 
		log.info("start sys-collector");
		this.startExporter();
		this.startAcquisitor();
		
		// 중단 파일이 touch 될때까지 대기
		FileUtil.waitForFileTouched(this.stopFile);
		
		// 정보 수집 중단
		log.info("terminate sys-collector");
		this.stopAcquisitor();;
		this.stopExporter();
	}
	
	/**
	 * 
	 */
	private void prepare() throws Exception {
		
		this.toExporterQueue = new LinkedBlockingQueue<>();
		
		this.cpuAcquisitor.setToExporterQueue(this.toExporterQueue);
		this.memAcquisitor.setToExporterQueue(this.toExporterQueue);
		this.diskAcquisitor.setToExporterQueue(this.toExporterQueue);
		this.netAcquisitor.setToExporterQueue(this.toExporterQueue);
		this.procAcquisitor.setToExporterQueue(this.toExporterQueue);
	}
	
	/**
	 * 
	 */
	private void startAcquisitor() throws Exception {
		
		//this.cpuAcquisitor.start();
		//this.memAcquisitor.start();
		//this.diskAcquisitor.start();
		this.netAcquisitor.start();
		//this.procAcquisitor.start();
	}
	
	private void stopAcquisitor() throws Exception {
		//this.cpuAcquisitor.stop();
		//this.memAcquisitor.stop();
		//this.diskAcquisitor.stop();
		//this.netAcquisitor.stop();
		this.procAcquisitor.stop();
	}

	/**
	 * 
	 */
	private void startExporter() throws Exception {
		
	}

	/**
	 * 
	 * @throws Exception
	 */
	private void stopExporter() throws Exception {
		
	}
}
