import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PrintSpooler {
    private String spoolFile;
    private int sleepForMs;
    private boolean printToStdout;
    private BufferedWriter writer;
    private Semaphore semaphore = new Semaphore(1);

    // construtor classe - recebe como parâmetro o nome do arquivo de spool
    public PrintSpooler(String spoolFile, int sleepForMs, boolean printToStdout) {
        this.spoolFile = spoolFile;
        this.sleepForMs = sleepForMs;
        this.printToStdout = printToStdout;
    }

    public PrintSpooler(String spoolFile, int sleepForMs) {
        this.spoolFile = spoolFile;
        this.sleepForMs = sleepForMs;
        this.printToStdout = false;
    }

    // método utilizado para abrir o arquivo de spool
    public boolean openPrintSpooler() {
        try {
            writer = new BufferedWriter(new FileWriter(spoolFile));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // método utilizado para imprimir um job
    public void printJob(String jobName) throws IOException {
        try {
            semaphore.acquire();
            System.out.println("Printing " + jobName);

            var headerSep = "-".repeat((80 - jobName.length() - 2) / 2);
            var footerSep = ".".repeat((80 - "END".length() - 2) / 2);

            var rawContent = headerSep + " " + jobName + " " + headerSep + "\n\n";

            rawContent += Files.readString(Paths.get(jobName), StandardCharsets.UTF_8).replaceAll("\r", "")
                    + "\n\n" + footerSep + " " + "END" + " " + footerSep + "\n\n";

            for (var c : rawContent.toCharArray()) {
                if (printToStdout) {
                    System.out.print(c);
                }

                writer.write(c);

                try {
                    Thread.sleep(sleepForMs);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
        }
    }

    // método utilizado para fechar o arquivo de spool
    public void closePrintSpooler() {
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int sleepForMs = 1;
        boolean printToStdout = false;

        try {
            sleepForMs = Integer.parseInt(args[0]);
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        try {
            printToStdout = Boolean.parseBoolean(args[1]);
        } catch (ArrayIndexOutOfBoundsException e) {
        }

        var spooler = new PrintSpooler("out/out.txt", sleepForMs, printToStdout);

        var threads = IntStream.range(1, 11)
                .mapToObj(i -> makeThread("in/" + i + ".txt", spooler))
                .collect(Collectors.toList());

        spooler.openPrintSpooler();

        threads.forEach(t -> t.start());
        threads.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
            }
        });

        spooler.closePrintSpooler();
    }

    static Thread makeThread(String filename, PrintSpooler spooler) {
        return new Thread(new Runnable() {
            public void run() {
                try {
                    var waitFor = ThreadLocalRandom.current().nextInt(0, 500);

                    System.out.println("Waiting for " + waitFor + " before printing " + filename);

                    Thread.sleep(waitFor);
                    spooler.printJob(filename);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}