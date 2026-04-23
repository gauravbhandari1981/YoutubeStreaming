package com.airos.streaming;

import java.util.Scanner;

public class StreamController {
    public static void main(String[] args) {
        // 1. Initialize our components
        StreamOrchestrator orchestrator = new StreamOrchestrator();
        StreamConsumer consumer = new StreamConsumer(orchestrator);

        // 2. Start the Consumer (It runs in its own VT, waiting for data)
        consumer.start();

        Scanner scanner = new Scanner(System.in);
        System.out.println("--- AIROS INGESTION COMMAND CENTER ---");
        
        while (true) {
            System.out.println("\nOptions: [1] Add Stream, [2] Remove Stream, [3] List Active, [4] Exit");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> {
                    System.out.print("Enter YouTube URL (or any name): ");
                    String url = scanner.nextLine();
                    orchestrator.addStream(url);
                }
                case "2" -> {
                    System.out.print("Enter ID to remove: ");
                    String id = scanner.nextLine();
                    orchestrator.removeStream(id);
                }
                case "3" -> orchestrator.listStreams();
                case "4" -> {
                    System.out.println("Shutting down engine...");
                    System.exit(0);
                }
                default -> System.out.println("Invalid command.");
            }
        }
    }
}