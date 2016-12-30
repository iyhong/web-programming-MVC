package spms.context;

// application-context.properties 를 통해 객체관리
public class ApplicationContext {
	private String propertiesPath;

	public ApplicationContext() {

	}

	public ApplicationContext(String propertiesPath) {
		this.propertiesPath = propertiesPath;
	}
}
