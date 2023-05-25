import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

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

            var commandList = List.of(input.split(" \\| "))
                .stream()
                .map(cmd -> cmd.trim().split(" "))
                .collect(Collectors.toList());
            
            var processBuilders = new ArrayList<ProcessBuilder>();
            for (var cmd : commandList) {
                var processBuilder = new ProcessBuilder(cmd);
                processBuilders.add(processBuilder);
            }

            var lastProcessBuilder = processBuilders.get(processBuilders.size() - 1);
            lastProcessBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            lastProcessBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
            try {
                var processes = ProcessBuilder.startPipeline(processBuilders);
                var lastProcess = processes.get(processes.size() - 1);
                lastProcess.waitFor();                
            } catch (IOException | InterruptedException e) {
                System.out.println(e.toString());
            }

            System.out.println();
        }

        scanner.close();
    }
}