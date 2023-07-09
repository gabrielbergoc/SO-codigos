import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Semaphore {
    private int count;

    public Semaphore(int count) {
        this.count = count;
    }

    public synchronized void down() {
        while (count <= 0) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        count--;
    }

    public void up() {
        count++;
    }

    public static void main(String[] args) {
        var threadCount = Integer.parseInt(args[0]);
        var resourceCount = Integer.parseInt(args[1]);

        var semaphore = new Semaphore(resourceCount);

        var threads = IntStream.range(0, threadCount)
                .mapToObj(i -> makeThread("#" + i, semaphore))
                .collect(Collectors.toList());

        threads.forEach(t -> t.start());
    }

    static Thread makeThread(String name, Semaphore semaphore) {
        return new Thread(new Runnable() {
            public void run() {
                try {
                    var waitFor = ThreadLocalRandom.current().nextInt(500);
                    System.out.println("Thread " + name + ": waiting for " + waitFor + "ms before acquiring semaphore");
                    Thread.sleep(waitFor);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                semaphore.down();

                System.out.println("Thread " + name + ": hello");

                try {
                    var waitFor = ThreadLocalRandom.current().nextInt(500);
                    System.out.println("Thread " + name + ": waiting for " + waitFor + "ms after acquiring semaphore");
                    Thread.sleep(waitFor);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                semaphore.up();
                System.out.println("Thread " + name + ": released semaphore");
            }
        });
    }
}