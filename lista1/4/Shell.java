import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

            try {
                var process = processBuilder.start();
                process.waitFor();

                String line = null;
                var iStream = new BufferedReader(new InputStreamReader(process.getInputStream()));
                while ((line = iStream.readLine()) != null) {
                    System.out.print(line);
                }
                var eStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                while ((line = eStream.readLine()) != null) {
                    System.out.print(line);
                }
            } catch (IOException | InterruptedException e) {
                System.out.println(e.toString());
            }

            System.out.println();
        }

        scanner.close();
    }
}