package com.redeye.sysexporter.acquisitor;

import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jutools.CronJob;
import com.jutools.StringUtil;

import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import oshi.SystemInfo;

/**
 * 
 * 
 * @author jmsohn
 */
@Slf4j
public abstract class Acquisitor {
	
	@Autowired
	private Environment environment;
	
	private String hostName;

	@Value("${app.schedule}")
	private String scheduleStr;
	
	private CronJob cronAcquisitor;
	
	@Setter
	@Getter
	private BlockingQueue<String> toExporterQueue;
	
	/** */
	@Getter(AccessLevel.PROTECTED)
	private SystemInfo sysInfo = new SystemInfo();
	
	/** */
	private ObjectMapper objMapper = new ObjectMapper();
	
	
	/**
	 * 
	 * 
	 * @return
	 */
	protected abstract String getName();
	
	/**
	 * 
	 * 
	 * @return 시스템 성능 정보 메시지
	 */
	protected abstract String acquireMetrics() throws Exception;
	
	/**
	 * 
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
	}

	/**
	 * 
	 */
	public void start() throws Exception {
		
		log.info("start " + this.getName());
		
		this.cronAcquisitor = new CronJob(
			this.scheduleStr,
			new Runnable() {
			
				@Override
				public void run() {
					try {
						
						//
						String message = acquireMetrics();
						
						//
						if(toExporterQueue != null) {
							
							if(message != null) {
								System.out.println("SEND:" + message);
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
		
		this.cronAcquisitor.run();
	}
	
	/**
	 * 
	 * 
	 * @param msgObj
	 * @return
	 */
	protected String toJSON(Map<String, Object> msgObj) throws Exception {
		
		if(msgObj == null) {
			throw new IllegalArgumentException("msgObj is null.");
		}
		
		msgObj.put("host", this.hostName);
		
		return this.objMapper.writeValueAsString(msgObj);
	}
	
	/**
	 * 
	 */
	public void stop() {
		
		log.info("stop " + this.getName());
		
		if(this.cronAcquisitor != null) {
			this.cronAcquisitor.stop();
		}
	}
}
