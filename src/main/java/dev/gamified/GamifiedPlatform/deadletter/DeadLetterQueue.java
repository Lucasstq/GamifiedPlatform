package dev.gamified.GamifiedPlatform.deadletter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class DeadLetterQueue {
    private static final Logger log = LoggerFactory.getLogger(DeadLetterQueue.class);
    private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();

    public void add(Runnable task) {
        queue.offer(task);
        log.warn("Task added to Dead Letter Queue: {}", task);
    }

    public Runnable poll() {
        return queue.poll();
    }

    public int size() {
        return queue.size();
    }
}

