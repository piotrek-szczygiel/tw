package dev.szczygiel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class WordCounter {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        var content = Files.readString(Paths.get("input.txt")).split(" ");

        System.out.println("Single thread: " + countSingleThread(content));
        System.out.println("Multi thread: " + countMultiThread(content));
    }

    private static long countSingleThread(String[] content) {
        return Arrays.stream(content).mapToLong(input -> {
            int stop = input.length();
            long count = 0;

            for (int i = 0; i < stop; ) {
                boolean word = false;

                while (i < stop && Character.isAlphabetic(input.charAt(i))) {
                    word = true;
                    ++i;
                }

                if (word) {
                    ++count;
                }

                while (i < stop && !Character.isAlphabetic(input.charAt(i))) {
                    ++i;
                }
            }

            return count;
        }).reduce(0, Long::sum);
    }

    private static long countMultiThread(String[] content) throws ExecutionException, InterruptedException {
        int threads = 4;
        var chunks = split(content, threads);
        var executor = Executors.newFixedThreadPool(threads);
        List<Future<Long>> futures = new ArrayList<>();

        for (var chunk : chunks) {
            futures.add(executor.submit(new Counter(chunk)));
        }

        long count = 0;
        for (var future : futures) {
            count += future.get();
        }

        executor.shutdown();

        return count;
    }

    private static List<String[]> split(String[] content, int threads) {
        List<String[]> chunks = new ArrayList<>();

        int chunkSize = content.length / threads;
        int remainder = content.length - threads * chunkSize;

        for (int i = 0; i < threads; ++i) {
            int from = i * chunkSize;
            int to = (i + 1) * chunkSize;

            if (i + 1 == threads) {
                to += remainder;
            }

            chunks.add(Arrays.copyOfRange(content, from, to));
        }

        return chunks;
    }

    static class Counter implements Callable<Long> {
        private final String[] input;

        Counter(String[] input) {
            this.input = input;
        }

        @Override
        public Long call() {
            return WordCounter.countSingleThread(input);
        }
    }
}
