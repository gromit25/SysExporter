package com.redeye.sysexporter.exporter.restapi;

@Component
@ConditionalOnProperty
(
  value = "app.exporter.type",
  havingValue = "RESTAPI"
)
public class RestAPIExporter extends Exporter {

  @Autowired
  private WebClient client;
  
  @Override
	public void send(String message) throws Exception {
  }
}
