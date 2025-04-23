package pc.crawler;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class Crawler implements Runnable{
	private ConcurrentHashMap<String, Boolean> set;
	private BlockingQueue<TaskData> queue;
	private Path outputDir;
	private String baseUrl;
	private ActivityMonitor act;
	
	public Crawler(ConcurrentHashMap<String, Boolean> set, BlockingQueue<TaskData> queue, Path outputDir, String baseUrl, ActivityMonitor act) {
		this.set = set;
		this.queue = queue;
		this.outputDir = outputDir;
		this.baseUrl = baseUrl;
		this.act=act;
	}

	@Override
	public void run() {
		while(true) {
			//TaskData task;
			try {
				TaskData task = queue.take();
				if(task == TaskData.POISON) break;
				if(!(task.getProf()==0)) {
					List<String> lS = WebCrawlerUtils.processUrl(task.getUrl(), baseUrl, outputDir);
					for(String s : lS) {
						if(!set.containsKey(s)) {
							act.taskStarted();
							set.put(s, true);
							queue.add(new TaskData(s, task.getProf()-1));
							System.out.println("queue size="+queue.size());
						}
					}
				}
				System.out.println("end of task "+task.getUrl());
				System.out.println("queue size="+queue.size());
				act.taskCompleted();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException|URISyntaxException e) {
				System.err.println("Error during crawling: " + e.getMessage());
				e.printStackTrace();
			}
			
		}
		
	}

}
