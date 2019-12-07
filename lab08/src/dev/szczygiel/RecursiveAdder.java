package dev.szczygiel;

import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.LongStream;

public class RecursiveAdder {
    public static void main(String[] args) {
        long[] input = new long[10_000_000];
        for (int i = 0; i < input.length; ++i) {
            input[i] = ThreadLocalRandom.current().nextLong(0, 1_000_000);
        }

        long result = new Adder(input).compute();
        System.out.println("Expected: " + LongStream.of(input).sum());
        System.out.println("Result:   " + result);
    }

    private static class Adder extends RecursiveTask<Long> {
        final private long[] input;

        final private int left;
        final private int right;

        Adder(long[] input, int left, int right) {
            this.input = input;
            this.left = left;
            this.right = right;
        }

        Adder(long[] input) {
            this.input = input;
            this.left = 0;
            this.right = input.length;
        }

        @Override
        protected Long compute() {
            assert left <= right;

            if (left == right - 1) {
                return input[left];
            }

            var sumLeft = new Adder(input, left, (left + right) / 2);
            sumLeft.fork();

            var sumRight = new Adder(input, (left + right) / 2, right);
            return sumRight.compute() + sumLeft.join();
        }
    }
}
