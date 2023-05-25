public class ReverseHello {

    public static void main(String[] args) {
        var max = args.length == 0 ? 50 : Integer.parseInt(args[0]);
        if (max < 0) {
            System.out.println("Usage: java ReverseHello.java <n>");
            System.out.println("n: positive integer");
            return;
        }

        var firstHello = new Hello(1, max);
        var firstThread = new Thread(firstHello);
        firstThread.start();
        try {
            firstThread.join();
        }
        catch (InterruptedException e) {
            System.out.println(e.toString());
        }
    }
    
    static class Hello implements Runnable {
        final int n;
        final int max;

        Hello(int n, int max) {
            this.n = n;
            this.max = max;
        }

        public void run() {
            if (n < max) {
                var newHello = new Hello(n + 1, max);
                var newThread = new Thread(newHello);
                newThread.start();

                try {
                    newThread.join();
                }
                catch (InterruptedException e) {
                    System.out.println(e.toString());
                }
            } 
            
            System.out.println("Hello from thread #" + n);
        }
    }
}
