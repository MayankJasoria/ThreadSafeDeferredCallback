import java.util.concurrent.ThreadLocalRandom;

public class Client {

    private static int counter;
    public static void main(String[] args) throws InterruptedException {
        counter = 0;
        DeferredCallbackExecutor executor = new DeferredCallbackExecutor();
        int numCallbacks = ThreadLocalRandom.current().nextInt(5, 10);
        System.out.println("Creating " + numCallbacks + " callbacks");
        for (int i = 0; i < numCallbacks; ++i) {
            executor.registerCallback(new Callback(() -> {
                System.out.println("Running callback");
                ++counter;
            }, getScheduledTime()));
        }

        System.out.println("Registered all callbacks, waiting for successful execution");

        while (counter < numCallbacks) {
            System.out.println("Number of callbacks run : " + counter);
            Thread.sleep(2_000);
        }
        System.out.println("Number of callbacks run: " + counter);
        executor.shutdown();
        executor.awaitTermination();
    }

    private static long getScheduledTime() {
        long time = System.currentTimeMillis() + ThreadLocalRandom.current().nextLong(5_000, 20_000);
        System.out.println("Scheduling callback at " + time);
        return time;
    }
}
