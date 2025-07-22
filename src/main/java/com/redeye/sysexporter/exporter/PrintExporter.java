package com.redeye.sysexporter.exporter;

/**
 * printer exporter 컴포넌트
 *
 * @author jmsohn
 */
public class PrintExporter extends Exporter {

	@Override
	public void send(String message) throws Exception {
		System.out.println("RECV: " + message);
	}
}
