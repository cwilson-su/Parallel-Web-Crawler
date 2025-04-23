package pc.crawler;

import java.util.concurrent.atomic.AtomicInteger;

public class ActivityMonitor {
	AtomicInteger cpt;
	
	public ActivityMonitor() {
		cpt= new AtomicInteger(0);
	}
	
	public void taskStarted() {
		int c =cpt.incrementAndGet();
		System.out.println(c);
	}
	
	public void taskCompleted() {
		int c =cpt.decrementAndGet();
		System.out.println(c);
		if(c==0) {
			notifyAll();
		}
	}
	public synchronized void awaitCompletion() {
		while(cpt.get()!=0) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
