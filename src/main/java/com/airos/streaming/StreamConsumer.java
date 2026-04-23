package com.airos.streaming;

import java.util.concurrent.TimeUnit;

public class StreamConsumer {
    private final StreamOrchestrator orchestrator;

    public StreamConsumer(StreamOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    public void start() {
        // Run the consumer in a long-lived Virtual Thread
        Thread.startVirtualThread(() -> {
            System.out.println("AI Consumer Service Started...");
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    // 1. Pull from the queue (Wait up to 1 second)
                    String segmentData = StreamOrchestrator.frameQueue.poll(1, TimeUnit.SECONDS);

                    if (segmentData != null) {
                        // 2. Spawn a NEW Virtual Thread for the AI work!
                        // This ensures one slow AI task doesn't block the next frame.
                        Thread.startVirtualThread(() -> processAI(segmentData));
                    }
                }
            } catch (InterruptedException e) {
                System.out.println("Consumer shutting down.");
            }
        });
    }

    private void processAI(String data) {
        try {
            // Simulate heavy AI computation (Object detection)
            // Virtual threads make this "wait" very cheap.
            long startTime = System.currentTimeMillis();
            Thread.sleep(500); // 500ms AI processing time
            long endTime = System.currentTimeMillis();
            
            System.out.println("[AI SUCCESS] Processed segment in " + (endTime - startTime) + "ms. Data: " + data.substring(0, 20) + "...");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}