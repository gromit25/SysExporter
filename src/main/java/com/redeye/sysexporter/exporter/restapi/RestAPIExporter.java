package com.redeye.sysexporter.exporter.restapi;

@Component
@ConditionalOnProperty
(
	value = "app.exporter.type",
	havingValue = "RESTAPI"
)
public class RestAPIExporter extends Exporter {

	private static final String SUBPATH_CPU = "";
	private static final String SUBPATH_MEM = "";
	private static final String SUBPATH_DISK = "";
	private static final String SUBPATH_NET = "";
	private static final String SUBPATH_PROCESS_TOP = "";

	@Autowired
	private WebClient client;

	
	@Override
	public void send(String message) throws Exception {
	}

	private void sendCpuUsage(Map<String, Object> msgObj) throws Excepiton {
	}

	private void sendMem(Map<String, Object> msgObj) throws Exception {
	}

	private void sendDisk(Map<String, Object> msgObj) throws Exception {
	}

	private void sendNetIO(Map<String, Object> msgObj) throws Exception {
	}

	private void sendProcessTop(Map<String, Object> msgObj) throws Exception {
	}
}
