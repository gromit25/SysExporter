package com.redeye.sysexporter.acquisitor;

import org.springframework.beans.factory.annotation.Value;

import com.jutools.CronJob;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import oshi.SystemInfo;

/**
 * 
 * 
 * @author jmsohn
 */
@Slf4j
public abstract class Acquisitor {

	@Value("${app.schedule}")
	private String scheduleStr;
	
	private CronJob cronAcquisitor;
	
	/** */
	@Getter(AccessLevel.PROTECTED)
	private SystemInfo sysInfo = new SystemInfo();
	
	/**
	 * 
	 */
	protected abstract void acquireMetrics();

	/**
	 * 
	 */
	public void start() throws Exception {
		
		log.info("start");
		
		this.cronAcquisitor = new CronJob(
			this.scheduleStr,
			new Runnable() {
			
				@Override
				public void run() {
					acquireMetrics();
				}
			}
		);
		
		this.cronAcquisitor.run();
	}

	/**
	 * 
	 */
	public void stop() {
		
		log.info("stop");
		
		if(this.cronAcquisitor != null) {
			this.cronAcquisitor.stop();
		}
	}
}
