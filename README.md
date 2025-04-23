# Parallel-Web-Crawler (Java 17+)

A tiny, production-style showcase of **multithreaded crawling** written for the LU3IN001 “Programmation concurrente” course at Sorbonne Université.  
Starting from a single URL the crawler explores the site breadth-first, downloads every PDF it meets, and stores the files locally—all while keeping workers busy with a shared, *blocking* queue.

## Why this project?
* **Practice with `ExecutorService` & thread pools** – distribute I/O-heavy work among N workers. :contentReference[oaicite:0]{index=0}&#8203;:contentReference[oaicite:1]{index=1}  
* **Master `BlockingQueue` patterns** – the queue acts as the central hand-off point between producers (discovered URLs) and consumers (download tasks). :contentReference[oaicite:2]{index=2}&#8203;:contentReference[oaicite:3]{index=3}  
* **Handle termination cleanly** – an `ActivityMonitor` counts outstanding tasks; when it reaches zero we push *poison pills* so idle workers exit without busy-waiting. :contentReference[oaicite:4]{index=4}&#8203;:contentReference[oaicite:5]{index=5}  
* **Avoid duplicate fetches** – a thread-safe `ConcurrentHashMap` remembers every visited URL. :contentReference[oaicite:6]{index=6}&#8203;:contentReference[oaicite:7]{index=7}  

## Architecture in 60 s

```text
┌────────────┐   task (URL,depth)    ┌────────────┐
│ Main thread│ ───────────────────▶ │ Blocking   │
│  submits   │               poll    │   Queue    │◀──┐
└────────────┘                       └────────────┘    │
        ▲            poison-pills ▲        │           │
        │                         │        ▼           │
        │                ┌───────────────────┐         │
        └────────────────┤  Worker threads   │  ◀─────┘
                         │  (Executor pool)  │
                         └───────────────────┘
                               │  download
                               ▼
                        local filesystem
```

Each queue element is a pair <url, depthRemaining>.

Workers repeatedly:
- take an element (blocking if empty),
- download any PDFs on the page,
- extract out-links; if depthRemaining > 0 push them back with depthRemaining – 1,
- call taskCompleted() on the shared ActivityMonitor.

When the monitor’s counter hits 0, the main thread inserts one poison pill per worker, waits for awaitTermination(), and exits.

## Running
### Plain javac (needs Java 17+)
```java
javac -d out $(find src -name "*.java")
java -classpath out pc.crawler.WebCrawlerParallel \
     --url "https://example.com"  \
     --depth 2                    \
     --threads 8                  \
     --out  "./downloads"
```
## Dependencies
- Java 17 or later
- No third-party libraries—only the standard JDK (java.net.http, java.nio, java.util.concurrent, …).
(Feel free to swap the parser for jsoup if you prefer HTML5 compliance.)

## Author
[cwilsonsu](https://github.com/cwilson-su)


Happy web crawling 🕷️






