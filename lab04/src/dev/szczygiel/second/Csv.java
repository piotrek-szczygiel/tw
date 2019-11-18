package dev.szczygiel.second;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

class Csv {
    private final Writer writer;
    private Map<Integer, List<Long>> values = new TreeMap<>();

    Csv(String path) throws FileNotFoundException {
        var file = new File(path);
        FileOutputStream stream = new FileOutputStream(file);
        writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8);
    }

    void save() throws IOException {
        writer.write("n;elapsed\n");

        for (var entry : values.entrySet()) {
            var n = entry.getKey();
            var list = entry.getValue();
            var average = (double) list.stream().reduce(0L, Long::sum) / (double) list.size();

            writer.write(String.format("%d;%.2f\n", n, average));
        }

        writer.flush();
        writer.close();
    }

    synchronized void add(int n, long elapsed) {
        if (!values.containsKey(n)) {
            values.put(n, new ArrayList<>());
        }

        var list = values.get(n);
        list.add(elapsed);
    }
}
