package com.redeye.sysexporter.exporter;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import lombok.extern.slf4j.Slf4j;

/**
 * exporter 추상 클래스
 * 
 * @author jmsohn
 */
@Slf4j
public abstract class Exporter {
	
	/** 중단 여부 */
	private volatile boolean stop = true;
	
	/** acquisitor -> exporter 연결 큐*/
	@Autowired
	@Qualifier("toExporterQueue")
	private BlockingQueue<String> toExporterQueue;
	
	/** exporter thread 객체 */
	private Thread exporterThread;
	
	
	/**
	 * exporter 발송 메소드
	 * 
	 * @param message 발송할 메시지
	 */
	public abstract void send(String message) throws Exception;
	
	/**
	 * exporter 스레드 시작 메소드
	 */
	public void start() throws Exception {

		// 중단 상태 변경
		this.stop = false;

		// exporter 스레드 생성
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

		// exporter 스레드 시작
		this.exporterThread.start();
	}
	
	/**
	 * exporter 스레드 중지
	 */
	public void stop() throws Exception {
		
		this.stop = true;
		
		if(exporterThread != null) {
			this.exporterThread.interrupt();
		}
	}
}
