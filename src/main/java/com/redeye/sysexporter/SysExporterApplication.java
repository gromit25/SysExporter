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
import com.redeye.sysexporter.exporter.KafkaExporter;

import lombok.extern.slf4j.Slf4j;

/**
 * 시스템 성능 정보 수집 메인 클래스
 * 
 * @author jmsohn
 */
@Slf4j
@SpringBootApplication
public class SysExporterApplication implements CommandLineRunner {
	
	@Value("${app.stop.file}")
	private File stopFile;
	
	/** exporter 전달용 큐(Acquisitor -> Exporter)*/
	private BlockingQueue<String> toExporterQueue;
	
	/** CPU 성능 수집기 */
	@Autowired
	private CPUMetricsAcquisitor cpuAcquisitor;
	
	/** 메모리 성능 수집기 */
	@Autowired
	private MemMetricsAcquisitor memAcquisitor;
	
	/** 네트워크 성는 수집기 */
	@Autowired
	private NetworkMetricsAcquisitor netAcquisitor;
	
	/** 디스크 성능 수집기 */
	@Autowired
	private DiskMetricsAcquisitor diskAcquisitor;
	
	/** 프로세스 성능 수집기 */
	@Autowired
	private ProcessMetricsAcquisitor procAcquisitor;
	
	/** 외부 출력기 */
	@Autowired
	private KafkaExporter exporter;
	
	
	/**
	 * 수집기 메인 메소드
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		SpringApplication.run(SysExporterApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		
		// 준비 작업
		log.info("prepare to start sys-collector");
		this.prepare();
		
		// 성능 수집기 시작
		log.info("start sys-collector");
		this.startExporter();
		this.startAcquisitor();
		
		// 중단 파일이 touch 될때까지 대기
		FileUtil.waitForFileTouched(this.stopFile);
		
		// 정보 수집 중단
		log.info("stop sys-collector");
		this.stopAcquisitor();;
		this.stopExporter();
	}
	
	/**
	 * 시작전 준비 작업
	 */
	private void prepare() throws Exception {
		
		this.toExporterQueue = new LinkedBlockingQueue<>();
		
		this.cpuAcquisitor.setToExporterQueue(this.toExporterQueue);
		this.memAcquisitor.setToExporterQueue(this.toExporterQueue);
		this.diskAcquisitor.setToExporterQueue(this.toExporterQueue);
		this.netAcquisitor.setToExporterQueue(this.toExporterQueue);
		this.procAcquisitor.setToExporterQueue(this.toExporterQueue);
		
		this.exporter.setToExporterQueue(this.toExporterQueue);
	}
	
	/**
	 * 성능 정보 수집 시작
	 */
	private void startAcquisitor() throws Exception {
		
		this.cpuAcquisitor.start();
		this.memAcquisitor.start();
		this.diskAcquisitor.start();
		this.netAcquisitor.start();
		//this.procAcquisitor.start();
	}
	
	/**
	 * 성능 정보 수집 중단
	 */
	private void stopAcquisitor() throws Exception {
		this.cpuAcquisitor.stop();
		this.memAcquisitor.stop();
		this.diskAcquisitor.stop();
		this.netAcquisitor.stop();
		//this.procAcquisitor.stop();
	}

	/**
	 * 외부 출력기 시작
	 */
	private void startExporter() throws Exception {
		this.exporter.start();
	}

	/**
	 * 외부 출력기 중단
	 */
	private void stopExporter() throws Exception {
		this.exporter.stop();
	}
}
