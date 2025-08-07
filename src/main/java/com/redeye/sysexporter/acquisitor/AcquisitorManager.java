package com.redeye.sysexporter.acquisitor;

import java.util.List;

import org.springframework.stereotype.Component;

/**
 * 시스템 성능 수집기 관리자
 *
 * @author jmsohn
 */
@Component
public class AcquisitorManager {

	/** 수집기 목록 */
	private List<Acquisitor> acquisitorList;

	/**
	 * 생성자
	 *
	 * @param acquisitorList 수집기 목록
	 */
	public AcquisitorManager(List<Acquisitor> acquisitorList) {
		this.acquisitorList = acquisitorList;
	}

	/**
	 * 수집기 실행
	 */
	public void run() throws Exception {
    
		if(this.acquisitorList == null) {
			return;
		}

		for(Acquisitor aquisitor: this.acquisitorList) {
			aquisitor.run();
		}
	}

	/**
	 * 수집기 중단
	 */
	public void stop() throws Exception {

		if(this.acquisitorList == null) {
			return;
		}

		for(Acquisitor aquisitor: this.acquisitorList) {
			aquisitor.stop();
		}
	}
}
