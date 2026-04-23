package com.airos.streaming;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;

public class StreamOrchestrator {
    // 5. Industry standard: Pushes to a BlockingQueue
    public static final BlockingQueue<String> frameQueue = new LinkedBlockingQueue<>(5000);
    
    // 6. Semaphore for backpressure handling
    public static final Semaphore systemSemaphore = new Semaphore(2000);
    
    // Registry to track multiple streams (Rule 4: same stream multiple times)
    private final Map<String, Thread> activeStreams = new ConcurrentHashMap<>();

    public void addStream(String streamUrl) {
        String uniqueId = "Stream-" + UUID.randomUUID().toString().substring(0, 8);
        
        // Start one long-lived VT for this specific stream
        Thread vt = Thread.startVirtualThread(() -> {
            System.out.println(">>> [INGESTOR] Started: " + uniqueId);
            runIngestionLoop(uniqueId, streamUrl);
        });
        
        activeStreams.put(uniqueId, vt);
    }

    private void runIngestionLoop(String id, String url) {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                // 3. Simulate pulling a 1-sec segment
                byte[] segment = simulateDownload(url);

                // 6. Semaphore check
                if (systemSemaphore.tryAcquire()) {
                    try {
                        // 5. Offer to the queue
                        frameQueue.offer(Base64.getEncoder().encodeToString(segment));
                    } finally {
                        systemSemaphore.release();
                    }
                } else {
                    System.err.println("!!! [BACKPRESSURE] System full, dropping segment for " + id);
                }
                
                Thread.sleep(Duration.ofSeconds(1)); // 1-sec interval
            }
        } catch (InterruptedException e) {
            System.out.println("<<< [INGESTOR] Stopped: " + id);
        }
    }

    private byte[] simulateDownload(String url) throws InterruptedException {
        Thread.sleep(100); // Simulate network latency
        return ("Segment-Data-from-" + url).getBytes();
    }

    public void removeStream(String id) {
        Thread t = activeStreams.remove(id);
        if (t != null) t.interrupt();
    }

    public void listStreams() {
        if (activeStreams.isEmpty()) System.out.println("No active streams.");
        activeStreams.forEach((id, t) -> System.out.println("Active ID: " + id));
    }
}