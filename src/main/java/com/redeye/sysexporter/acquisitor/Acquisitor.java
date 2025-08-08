package com.redeye.sysexporter.acquisitor;

import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jutools.CronJob;
import com.jutools.StringUtil;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import oshi.SystemInfo;

/**
 * 수집기 추상 클래스
 * 
 * @author jmsohn
 */
@Slf4j
public abstract class Acquisitor {
	
	/** 중단 상태 */
	private volatile boolean stop = true;
	
	/** 스프링부트 환경 변수 객체 */
	@Autowired
	private Environment environment;

	/** 조직 : host 구분자 */
	@Value("${app.organ}")
	@Pattern(regexp="^[a-zA-Z0-9_\\-]{0, 200}$", message = "invalid organization name.")
	private String organ;

	/** 영역 : host 구분자 */
	@Value("${app.region}")
	@Pattern(regexp="^[a-zA-Z0-9_\\-]{0, 200}$", message = "invalid region name.")
	private String region;
	
	/**
	 * 호스트 명<br>
	 * application.properties의 app.host 로 설정 가능<br>
	 * 설정이 되지 않았거나 빈값일 경우, 컴퓨터 명을 디폴트로 설정됨
	 */
	private String hostName;

	/** 수집 주기 */
	@Value("${app.schedule}")
	private String scheduleStr;
	
	/** 수집기 크론잡 */
	private CronJob cronAcquisitor;

	/** exporter 큐 */
	@Autowired
	@Qualifier("toExporterQueue")
	private BlockingQueue<String> toExporterQueue;
	
	/** 시스템 정보를 읽기 위한 OSHI 객체 */
	@Getter(AccessLevel.PROTECTED)
	private SystemInfo sysInfo = new SystemInfo();
	
	/** JSON 메시지를 만들기 위한 object mapper */
	private ObjectMapper objMapper = new ObjectMapper();
	
	
	/**
	 * 수집기 명 반환
	 * 
	 * @return 수집기 명
	 */
	protected abstract String getName();
	
	/**
	 * 성능 정보 수집 후 봔환
	 * 
	 * @return 시스템 성능 정보 객체
	 */
	protected abstract Map<String, Object> acquireMetrics() throws Exception;
	
	/**
	 * 컴포넌트 생성 후 초기화
	 */
	@PostConstruct
	public void init() {
		
		this.hostName = environment.getProperty("app.host");
		
		if(StringUtil.isBlank(this.hostName) == true) {
			
			String runtimeName = ManagementFactory.getRuntimeMXBean().getName();
			
			if(runtimeName != null) {
				
				String[] splitedRuntimeName = runtimeName.split("@");
				
				if(splitedRuntimeName.length > 1) {
					this.hostName = splitedRuntimeName[1];
				} else {
					this.hostName = "N/A";
				}
			}
		}
		
		log.info("host name:" + this.hostName);
	}

	/**
	 * 수집 스레드 시작
	 */
	public void run() throws Exception {
		
		if(this.stop == false) {
			throw new IllegalStateException("thread is already started.");
		}
		
		log.info("start " + this.getName());
		this.stop = false;
		
		// 수집 크론잡 생성
		this.cronAcquisitor = new CronJob(
			this.scheduleStr,
			new Runnable() {
			
				@Override
				public void run() {
					try {
						
						// 성능 데이터 수집
						Map<String, Object> msgMap = acquireMetrics();
						
						// 수집된 성능 데이터를 큐로 전송
						if(toExporterQueue != null) {
							
							if(msgMap != null) {
								
								String message = toJSON(msgMap);
								toExporterQueue.put(message);
							}
							
						} else {
							log.info("toExporterQueue is null.");
						}
						
					} catch (Exception ex) {
						log.error("exception at " + getName(), ex);
					}
				}
			}
		);
		
		// 수집 크론잡 실행
		this.cronAcquisitor.run();
	}
	
	/**
	 * 수집 데이터 Map 객체를 JSON 문자열로 변환
	 * 
	 * @param msgMap 수집 데이터 맵 객체 
	 * @return 변환된 JSON 문자열
	 */
	protected String toJSON(Map<String, Object> msgMap) throws Exception {
		
		if(msgMap == null) {
			throw new IllegalArgumentException("msgMap is null.");
		}

		// 기준 시간 및 호스트 정보 추가
		msgMap.put("timestamp", this.cronAcquisitor.getCurrentBaseTime());
		msgMap.put("type", this.getName());
		msgMap.put("organ", this.organ);
		msgMap.put("region", this.region);
		msgMap.put("host", this.hostName);

		// JSON 형태로 변환
		return this.objMapper.writeValueAsString(msgMap);
	}
	
	/**
	 * 수집 프로세스 중지
	 */
	public void stop() throws Exception {
		
		if(this.stop == true) {
			throw new IllegalStateException("thread is already stopped.");
		}
		
		log.info("stop " + this.getName());
		this.stop = true;
		
		if(this.cronAcquisitor != null) {
			this.cronAcquisitor.stop();
		}
	}
}
