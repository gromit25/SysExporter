package com.redeye.sysexporter.exporter.restapi;

@Configuration
@ConditionalOnProperty
(
	value = "app.exporter.type",
	havingValue = "RESTAPI"
}
public class RestAPIExporterConfig {
  
	@Bean("webClient")
	public WebClient webClient(
		@Value("app.exporter.restapi.url") String url
	) {
		return WebClient.builder()
			.baseUrl(url)
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.build();
	}
}
