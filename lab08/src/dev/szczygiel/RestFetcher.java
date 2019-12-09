package dev.szczygiel;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RestFetcher {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        long startMulti = System.currentTimeMillis();
        fetchAll(50);
        long elapsedMulti = System.currentTimeMillis() - startMulti;
        System.out.println("Multithreaded: " + elapsedMulti + "ms");

        long startSingle = System.currentTimeMillis();
        fetchAll(1);
        long elapsedSingle = System.currentTimeMillis() - startSingle;
        System.out.println("Singlethreaded: " + elapsedSingle + "ms");
        System.out.println("Multithreaded is faster " + (float) elapsedSingle / elapsedMulti + " times");
    }

    private static void fetchAll(int threads) throws ExecutionException, InterruptedException {
        System.out.println("Fetching all posts using " + threads + " thread" + (threads > 1 ? "s" : ""));

        var executor = Executors.newFixedThreadPool(threads);
        List<Future<String>> futures = new ArrayList<>();

        for (int i = 0; i < 200; ++i) {
            int id = i % 100 + 1;
            futures.add(executor.submit(() -> fetchPost(id)));
        }

        for (int i = 0; i < futures.size(); ++i) {
            var future = futures.get(i);
            if (i % 5 == 0) {
                System.out.print('.');
            }
            if (future.get() == null) {
                System.out.println();
                System.err.println(i + " failed");
            }
        }

        executor.shutdown();
        System.out.println();
        System.out.println("Done fetching all posts using " + threads + " thread" + (threads > 1 ? "s" : ""));
    }

    private static String fetchPost(int id) throws IOException {
        assert id > 0 && id <= 100;

        URL url = new URL("http://jsonplaceholder.typicode.com/posts/" + id);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setUseCaches(false);
        connection.setRequestMethod("GET");
        connection.connect();

        int code = connection.getResponseCode();
        if (code == 200) {
            return new String(connection.getInputStream().readAllBytes()).replace('\n', ' ');
        }

        return null;
    }
}
