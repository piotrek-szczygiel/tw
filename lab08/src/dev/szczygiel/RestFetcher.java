package dev.szczygiel;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;

public class RestFetcher {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        fetchAll(200);
        System.out.println();
        fetchAll(1);
    }

    private static void fetchAll(int threads) throws ExecutionException, InterruptedException {
        System.out.println("Fetching all posts using " + threads + " thread" + (threads > 1 ? "s" : ""));

        var executor = Executors.newFixedThreadPool(threads);
        List<Future<String>> futures = new ArrayList<>();

        for (int i = 0; i < 200; ++i) {
            int id = ThreadLocalRandom.current().nextInt(1, 100);
            futures.add(executor.submit(() -> fetchPost(id)));
        }

        for (int i = 0; i < futures.size(); ++i) {
            var future = futures.get(i);
            System.out.print('.');
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
