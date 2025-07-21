package com.redeye.sysexporter.exporter;

import org.springframework.stereotype.Component;

@Component
public class PrintExporter extends Exporter {

	@Override
	public void send(String message) throws Exception {
		System.out.println(message);
	}
}
