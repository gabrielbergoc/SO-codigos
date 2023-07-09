import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Toalete {
    private int count = 0;

    public synchronized void homemQuerEntrar(String nome) {
        while (count > 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        count--;
    }

    public synchronized void mulherQuerEntrar(String nome) {
        while (count < 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        count++;
    }

    public synchronized void homemQuerSair(String nome) {
        count++;
        notifyAll();
    }

    public synchronized void mulherQuerSair(String nome) {
        count--;
        notifyAll();
    }

    public static void main(String[] args) {
        var threadCount = Integer.parseInt(args[0]);

        var toilet = new Toalete();

        var threadsMen = IntStream.range(0, threadCount)
                .mapToObj(i -> makeThreadMale("#" + i, toilet))
                .collect(Collectors.toList());
        threadsMen.forEach(t -> t.start());

        var threadsWomen = IntStream.range(0, threadCount)
                .mapToObj(i -> makeThreadFemale("#" + i, toilet))
                .collect(Collectors.toList());
        threadsWomen.forEach(t -> t.start());
    }

    static Thread makeThreadMale(String name, Toalete toilet) {
        return new Thread(new Runnable() {
            public void run() {
                try {
                    var waitFor = ThreadLocalRandom.current().nextInt(500);
                    System.out.println("Homem " + name + ": esperando " + waitFor + "ms antes de entrar no toalete");
                    Thread.sleep(waitFor);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                toilet.homemQuerEntrar(name);
                System.out.println("Homem " + name + ": entrou no toalete");

                try {
                    var waitFor = ThreadLocalRandom.current().nextInt(500);
                    System.out.println("Homem " + name + ": usando toalete por " + waitFor + "ms");
                    Thread.sleep(waitFor);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("Homem " + name + ": saindo do toalete");
                toilet.homemQuerSair(name);
            }
        });
    }

    static Thread makeThreadFemale(String name, Toalete toilet) {
        return new Thread(new Runnable() {
            public void run() {
                try {
                    var waitFor = ThreadLocalRandom.current().nextInt(500);
                    System.out.println("Mulher " + name + ": esperando " + waitFor + "ms antes de entrar no toalete");
                    Thread.sleep(waitFor);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                toilet.mulherQuerEntrar(name);
                System.out.println("Mulher " + name + ": entrou no toalete");

                try {
                    var waitFor = ThreadLocalRandom.current().nextInt(500);
                    System.out.println("Mulher " + name + ": usando toalete por " + waitFor + "ms");
                    Thread.sleep(waitFor);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("Mulher " + name + ": saindo do toalete");
                toilet.mulherQuerSair(name);
            }
        });
    }
}
