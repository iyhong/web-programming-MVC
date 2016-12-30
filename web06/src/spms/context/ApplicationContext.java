package spms.context;

import java.util.Hashtable;
import java.util.Map;

// application-context.properties 를 통해 객체관리
public class ApplicationContext {
	Map<String, Object> objTable = new Hashtable<String, Object>();
	private String propertiesPath;

	public ApplicationContext() {
	}

	public ApplicationContext(String propertiesPath) {
		this.propertiesPath = propertiesPath;
	}
	
}
