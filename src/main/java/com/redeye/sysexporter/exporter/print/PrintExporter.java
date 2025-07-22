package com.redeye.sysexporter.exporter.print;

import com.redeye.sysexporter.exporter.Exporter;

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
