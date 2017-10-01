package com.github.lfyuomr.gylo.bostongene.task1;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.PriorityQueue;

public class MyThreadSafePriorityQueue<T> {
    private final PriorityQueue<T> queue;
    private final Object lock = new Object();

    public MyThreadSafePriorityQueue() {
        this.queue = new PriorityQueue<>();
    }

    public MyThreadSafePriorityQueue(Comparator<? super T> comparator) {
        this.queue = new PriorityQueue<T>(comparator);
    }

    public boolean push(T t) {
        synchronized (lock) {
            queue.add(t);
            lock.notify();
            return true;
        }
    }

    @Nullable T popNonBlocking() {
        synchronized (lock) {
            return queue.poll();
        }
    }

    @NotNull T popBlocking() throws InterruptedException {
        synchronized (lock) {
            if (queue.size() != 0) {
                return queue.poll();
            } else {
                while (queue.size() == 0) {
                    lock.wait();
                }
                return queue.poll();
            }
        }
    }
}
