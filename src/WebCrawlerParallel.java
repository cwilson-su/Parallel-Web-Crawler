package pc.crawler;
import java.nio.file.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.io.*;


public class WebCrawlerParallel {
    public static void main(String[] args) {
        // Hardcoded base URL to start crawling from
        String baseUrl = "https://www-licence.ufr-info-p6.jussieu.fr/lmd/licence/2023/ue/LU3IN001-2023oct/index.php";
        
        // Hardcoded output directory where downloaded pages will be saved
        Path outputDir = Paths.get("/tmp/crawler/");
        
        ConcurrentHashMap<String, Boolean> set = new ConcurrentHashMap<>();
        
        BlockingQueue<TaskData> queue = new LinkedBlockingQueue<TaskData>();
        
        ExecutorService pool = Executors.newFixedThreadPool(8);
        
        ActivityMonitor act = new ActivityMonitor();
        
        try {
            // Ensure the output directory exists; create it if it doesn't
            if (!Files.exists(outputDir)) {
                Files.createDirectories(outputDir);
                System.out.println("Created output directory: " + outputDir.toAbsolutePath());
            }
            long start = System.currentTimeMillis();
            act.taskStarted();
            queue.add(new TaskData(baseUrl, 4));
            
            for(int i=0; i<8; i++) {
            	pool.submit(new Crawler(set, queue, outputDir, baseUrl, act));
            }
            
            act.awaitCompletion();
            
            for(int i=0; i<15; i++) {
            	queue.add(TaskData.POISON);
            }
            pool.shutdown();
            
            System.out.println("Rendered image in " + (System.currentTimeMillis() - start) + " ms");


        } catch (IOException e) {
            // Handle exceptions that may occur during crawling
            System.err.println("Error during crawling: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
