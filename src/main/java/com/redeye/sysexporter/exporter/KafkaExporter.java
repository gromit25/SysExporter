package com.redeye.sysexporter.exporter;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

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
	@Autowired
	@Qualifier("toExporterQueue")
	private BlockingQueue<String> toExporterQueue;
	
	/** */
	private Thread exporterThread;
	
	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	
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
						
						kafkaTemplate.send("sys_metrics", message);
						
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
