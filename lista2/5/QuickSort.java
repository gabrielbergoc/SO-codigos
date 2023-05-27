import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class QuickSort {
    static final ThreadGroup threadGroup = new ThreadGroup("quicksort");

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: quicksort <x1> <x2> ... <xN>");
            return;
        }

        var nums = List.of(args).stream()
                .mapToInt(Integer::parseInt)
                .toArray();

        var sort = new Sort(nums, 0, nums.length - 1);
        var thread = new Thread(threadGroup, sort);
        thread.start();

        while (threadGroup.activeCount() > 0) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        System.out.println(Arrays.toString(nums));
    }

    static class Sort implements Runnable {
        int[] nums;
        int start;
        int end;

        Sort(int[] nums, int start, int end) {
            this.nums = nums;
            this.start = start;
            this.end = end;
        }

        public void run() {
            quickSort();
        }

        private void quickSort() {
            int l = start;
            int r = end;
            int midValue = nums[(l + r) / 2];

            while (l <= r) {
                while (nums[l] < midValue && l < end) {
                    l++;
                }
                while (nums[r] > midValue && r > start) {
                    r--;
                }

                if (l <= r) {
                    int temp = nums[l];
                    nums[l] = nums[r];
                    nums[r] = temp;
                    l++;
                    r--;
                }
            }

            if (r > start) {
                var newSort = new Sort(nums, start, r);
                var newThread = new Thread(threadGroup, newSort);
                newThread.start();
            }
            if (l < end) {
                var newSort = new Sort(nums, l, end);
                var newThread = new Thread(threadGroup, newSort);
                newThread.start();
            }
        }
    }

}
