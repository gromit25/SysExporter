package com.redeye.sysexporter.exporter;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 
 * @author jmsohn
 */
@Slf4j
public abstract class Exporter {
	
	/** */
	private volatile boolean stop = true;
	
	/** */
	@Autowired
	@Qualifier("toExporterQueue")
	private BlockingQueue<String> toExporterQueue;
	
	/** */
	private Thread exporterThread;
	
	
	/**
	 * 
	 * 
	 * @param message
	 */
	public abstract void send(String message) throws Exception;
	
	/**
	 * 
	 */
	public void start() throws Exception {
		
		this.stop = false;
		
		this.exporterThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				while(stop == false && Thread.currentThread().isInterrupted() == false) {
					
					String message = null;
				
					try {
						
						message = toExporterQueue.poll(1000L, TimeUnit.MILLISECONDS);
						if(message == null) {
							continue;
						}
						
						send(message);
						
					} catch(InterruptedException iex) {
						
						stop = true;
						Thread.currentThread().interrupt();
						
					} catch(Exception ex) {
						
						log.error("exception", ex);
					}
				}
			}
		});
		
		this.exporterThread.start();
	}
	
	/**
	 * 
	 */
	public void stop() throws Exception {
		
		this.stop = true;
		
		if(exporterThread != null) {
			this.exporterThread.interrupt();
		}
	}
}
