package com.redeye.sysexporter.exporter.restapi;

/**
 *
 *
 * @author jmsohn
 */
@Component
@ConditionalOnProperty
(
	value = "app.exporter.type",
	havingValue = "RESTAPI"
)
public class RestAPIExporter extends Exporter {

	/** cpu 사용율 api subpath */
	private static final String SUBPATH_CPU = "/host/%s/metrics/cpu";
	
	/** memory 사용율 api subpath */
	private static final String SUBPATH_MEM = "/host/%s/metrics/memory";
	
	/** disk 사용율 api subpath */
	private static final String SUBPATH_DISK = "/host/%s/metrics/disk";
	
	/** network io 사용율 api subpath */
	private static final String SUBPATH_NETIO = "/host/%s/metrics/netio";
	
	/** process top 5 api subpath */
	private static final String SUBPATH_PROCESS_TOP = "/host/%s/metrics/process/top";

	/** rest api 연결 클라이언트 */
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
