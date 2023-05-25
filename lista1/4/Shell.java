import java.io.IOException;
import java.util.Scanner;

public class Shell {

    public static void main(String[] args) {
        var scanner = new Scanner(System.in);
        String input;
        while (true) {
            System.out.print(">>> ");

            input = scanner.nextLine();
            if (input == null) {
                break;
            }

            var childArgs = input.split(" ");
            var processBuilder = new ProcessBuilder(childArgs);
            processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);

            try {
                var process = processBuilder.start();
                process.waitFor();
            } catch (IOException | InterruptedException e) {
                System.out.println(e.toString());
            }

            System.out.println();
        }

        scanner.close();
    }
}