package com.github.lfyuomr.gylo.bostongene.task1;

import lombok.val;

import java.io.PrintStream;

public class Retreiver {
    private final PrintStream out;
    private final MyThreadSafePriorityQueue in;
    private final boolean blocking;
    private final int intervalMillis;

    private volatile boolean continueRunning = true;

    public Retreiver(PrintStream out, MyThreadSafePriorityQueue in, boolean blocking, int intervalMillis) {
        this.out = out;
        this.in = in;
        this.blocking = blocking;
        this.intervalMillis = intervalMillis;
    }

    public void run() {
        while (continueRunning) {
            try {
                if (blocking) {
                    blockingNext();
                } else {
                    nonBlockingNext();
                }

                Thread.sleep(intervalMillis);
            } catch (InterruptedException e) {
                continueRunning = false;
                Thread.currentThread().interrupt();
            }
        }
    }

    public void stop() {
        continueRunning = false;
    }

    private void blockingNext() throws InterruptedException {
        val num = in.popBlocking();
        System.out.println("> текущий минимум: " + num);
    }

    private void nonBlockingNext() {
        val num = in.popNonBlocking();
        System.out.println(num == null ? "> в очереди нет ни одного числа" : ("> текущий минимум:" + num));
    }
}
