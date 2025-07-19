package com.redeye.sysexporter.exporter;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * 
 * @author jmsohn
 */
@Component
public class KafkaExporter {
	
	/** */
	private volatile boolean stop = true;
	
	/** */
	@Getter
	@Setter
	private BlockingQueue<String> toExporterQueue;
	
	/** */
	private Thread exporterThread;

	
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
						
						System.out.println("RECV:" + message);
						
					} catch(InterruptedException iex) {
						stop = true;
						Thread.currentThread().interrupt();
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
