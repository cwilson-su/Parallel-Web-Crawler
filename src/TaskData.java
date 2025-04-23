package pc.crawler;

public class TaskData {
	private final String url;
	private final int prof;
	public static final TaskData POISON = new TaskData(null, 0);
	
	public TaskData(String url, int prof) {
		this.url = url;
		this.prof = prof;
	}

	public String getUrl() {
		return url;
	}

	public int getProf() {
		return prof;
	}
	
	
}
