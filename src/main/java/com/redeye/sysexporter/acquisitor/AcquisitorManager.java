package com.redeye.sysexporter.acquisitor;

/**
 *
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

		System.out.println("DEBUG 100:" + this.acqusitiorList.size());
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
