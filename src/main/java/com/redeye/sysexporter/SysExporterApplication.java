package com.redeye.sysexporter;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.jutools.FileUtil;
import com.redeye.sysexporter.acquisitor.AcquisitorManager;
import com.redeye.sysexporter.exporter.Exporter;

import lombok.extern.slf4j.Slf4j;

/**
 * 시스템 성능 정보 수집 메인 클래스<br>
 * 수집기(Acquisitor -> BlockingQueue -> Exporter)
 * 
 * @author jmsohn
 */
@Slf4j
@SpringBootApplication
public class SysExporterApplication implements CommandLineRunner {

	/** 중단 파일 - touch 될 경우 프로그램 중지됨 */
	@Value("${app.stop.file}")
	private File stopFile;
	
	/** 시스템 성능 수집기 관리자 */
	@Autowired
	private AcquisitorManager acquisitorManager;
	
	/** 외부 출력기 */
	@Autowired
	@Qualifier("exporter")
	private Exporter exporter;
	
	
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
		
		// 성능 수집기 시작
		log.info("start sys-collector");
		this.exporter.run();
		this.acquisitorManager.run();
		
		// 중단 파일이 touch 될때까지 대기
		FileUtil.waitForFileTouched(this.stopFile);
		
		// 정보 수집 중단
		log.info("stop sys-collector");
		this.acquisitorManager.stop();
		this.exporter.stop();
	}
}
