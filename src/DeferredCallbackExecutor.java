import java.util.Objects;
import java.util.PriorityQueue;

public class DeferredCallbackExecutor {

    private final PriorityQueue<Callback> pq;
    private Thread executorThread;
    private boolean isShutdown = false;

    public DeferredCallbackExecutor() {
        pq = new PriorityQueue<>();
        executorThread = null;
    }

    private long computeSleepTime(long startTime) {
        return startTime - System.currentTimeMillis();
    }

    public void registerCallback(Callback callback) {
        synchronized (this) {
            if (isShutdown) {
                throw new IllegalStateException("Cannot register callback on an executor that is shutting down");
            }
        }
        synchronized (pq) {
            if (Objects.isNull(executorThread)) {
                init();
            }
            pq.offer(callback);
            pq.notify();
        }
    }

    public synchronized void shutdown() {
        isShutdown = true;
        synchronized (pq) {
            // ensure executor thread receives notification
            pq.notify();
        }
    }

    public boolean awaitTermination() throws InterruptedException {
        if (Objects.isNull(executorThread)) {
            return true;
        }
        executorThread.join();
        return true;
    }

    private void init() {
        executorThread = new Thread(new ExecutorThread());
        executorThread.setName("ExecutorThread");
        executorThread.start();
    }

    private class ExecutorThread implements Runnable {
        @Override
        public void run() {
            outer: while (!isShutdown || !pq.isEmpty()) {
                synchronized (pq) {
                    try {
                        while (pq.isEmpty()) {
                            if (isShutdown) {
                                break outer;
                            }
                            pq.wait();
                        }
                        long diff = computeSleepTime(pq.peek().executeAt());
                        while (diff > 0) {
                            pq.wait(diff);
                            assert pq.peek() != null;
                            diff = computeSleepTime(pq.peek().executeAt());
                        }
                        Callback callback = Objects.requireNonNull(pq.poll());
                        System.out.println("Callback scheduled at " + callback.executeAt() + " is running at " +
                                System.currentTimeMillis());
                        Thread thread = new Thread(callback.runnable());
                        thread.setName("Callback Executor " + thread.getId());
                        thread.start();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            System.out.println("Shutting down executor");
        }
    }
}
