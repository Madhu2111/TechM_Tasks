package com.ThreadExamples;

import java.util.Scanner;

class PrimeSumTask implements Runnable {
    private int start;
    private int end;
    private long sum;
    public PrimeSumTask(int start, int end) {
        this.start = start;
        this.end = end;
        this.sum = 0;
    }
    public long getSum() {
        return sum;
    }
    public void run() {
        for (int i = start; i <= end; i++) {
            if (isPrime(i)) {
                sum += i;
            }
        }
    }
    private boolean isPrime(int num) {
        if (num <= 1) {
            return false;
        }
        for (int i = 2, sqrt = (int) Math.sqrt(num); i <= sqrt; i++) {
            if (num % i == 0) {
                return false;
            }
        }
        return true;
    }
}
public class ThreadEx5 {
    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter a limit:");
        int limit = scanner.nextInt();
        scanner.close();

        int numThreads = Runtime.getRuntime().availableProcessors();
        PrimeSumTask[] tasks = new PrimeSumTask[numThreads];
        Thread[] threads = new Thread[numThreads];

        int chunkSize = limit / numThreads;
        for (int i = 0; i < numThreads; i++) {
            int start = i * chunkSize + 1;
            int end = (i == numThreads - 1) ? limit : (start + chunkSize - 1);
            tasks[i] = new PrimeSumTask(start, end);
            threads[i] = new Thread(tasks[i]);
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        long sum = 0;
        for (PrimeSumTask task : tasks) {
            sum += task.getSum();
        }

        System.out.println("Sum of prime numbers up to " + limit + ": " + sum);
    }
}
