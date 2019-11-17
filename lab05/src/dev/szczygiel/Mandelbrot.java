package dev.szczygiel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Mandelbrot extends JFrame {
    private int WIDTH = 800;
    private int HEIGHT = 600;
    private BufferedImage I = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

    private Mandelbrot() {
        super("Mandelbrot");

        setBounds(100, 100, WIDTH, HEIGHT);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        var mandelbrot = new Mandelbrot();
        mandelbrot.setVisible(true);

        var result = new HashMap<String, ArrayList<Long>>();
        result.put("1 -> 1", new ArrayList<>());
        result.put("4 -> 4", new ArrayList<>());
        result.put("8 -> 8", new ArrayList<>());

        result.put("1 -> 10", new ArrayList<>());
        result.put("4 -> 40", new ArrayList<>());
        result.put("8 -> 80", new ArrayList<>());

        result.put("1 -> p", new ArrayList<>());
        result.put("4 -> p", new ArrayList<>());
        result.put("8 -> p", new ArrayList<>());

        for (int i = 0; i < 10; ++i) {
            result.get("1 -> 1").add(mandelbrot.run(1, 1, false));
            result.get("4 -> 4").add(mandelbrot.run(4, 4, false));
            result.get("8 -> 8").add(mandelbrot.run(8, 8, false));

            result.get("1 -> 10").add(mandelbrot.run(1, 10, false));
            result.get("4 -> 40").add(mandelbrot.run(4, 40, false));
            result.get("8 -> 80").add(mandelbrot.run(8, 80, false));

            result.get("1 -> p").add(mandelbrot.run(1, 0, true));
            result.get("4 -> p").add(mandelbrot.run(4, 0, true));
            result.get("8 -> p").add(mandelbrot.run(8, 0, true));

            System.out.println("Completed iteration " + (i + 1));
        }

        mandelbrot.setStatus("Finished");

        for (var entry : result.entrySet()) {
            System.out.println();
            System.out.println(entry.getKey());
            System.out.println("------------");

            for (var elapsed : entry.getValue()) {
                System.out.println(elapsed);
            }
        }
    }

    private long run(int threads, int chunks, boolean pixel) throws ExecutionException, InterruptedException {
        if (pixel) {
            setStatus("Pool: " + threads + "    For every pixel");
        } else {
            setStatus("Pool: " + threads + "    Chunks: " + chunks);
        }

        I = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        repaint();

        var pool = Executors.newFixedThreadPool(threads);
        var futures = new LinkedList<Future<CalculationResult>>();

        var start = System.currentTimeMillis();

        if (pixel) {
            for (int y = 0; y < HEIGHT; ++y) {
                for (int x = 0; x < WIDTH; ++x) {
                    futures.add(pool.submit(new Calculation(x, y, 1, 1)));
                }
            }
        } else {
            int width = WIDTH / chunks;
            for (int chunk = 0; chunk < chunks; ++chunk) {
                int xx = chunk * width;
                futures.add(pool.submit(new Calculation(xx, 0, width, HEIGHT)));
            }
        }

        while (!futures.isEmpty()) {
            var iter = futures.iterator();
            while (iter.hasNext()) {
                var future = iter.next();
                if (future.isDone()) {
                    drawImage(future.get());
                    iter.remove();
                }
            }
        }

        return System.currentTimeMillis() - start;
    }

    private void setStatus(String status) {
        setTitle("Mandelbrot    " + status);
    }

    private void drawImage(CalculationResult result) {
        I.getGraphics().drawImage(result.I, result.xx, result.yy, null);
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(I, 0, 0, this);
    }

    private static class CalculationResult {
        BufferedImage I;
        int xx;
        int yy;
    }

    private static class Calculation implements Callable<CalculationResult> {
        private final int xx;
        private final int yy;
        private final int width;
        private final int height;

        Calculation(int xx, int yy, int width, int height) {
            this.xx = xx;
            this.yy = yy;
            this.width = width;
            this.height = height;
        }

        @Override
        public CalculationResult call() {
            var result = new CalculationResult();
            result.I = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            result.xx = xx;
            result.yy = yy;

            double ZOOM = 150;

            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    double zx = 0;
                    double zy = 0;
                    double cx = (xx + x - 400) / ZOOM;
                    double cy = (yy + y - 300) / ZOOM;

                    int iter = 10000;
                    while (zx * zx + zy * zy < 4 && iter > 0) {
                        double tmp = zx * zx - zy * zy + cx;
                        zy = 2.0 * zx * zy + cy;
                        zx = tmp;
                        iter--;
                    }

                    result.I.setRGB(x, y, iter | (iter << 8));
                }
            }

            return result;
        }
    }
}
