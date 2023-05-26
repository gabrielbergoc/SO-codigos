package matrix;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class MatrixDeterminant {
    public static void main(String[] args) {
        if (args.length != 1 && args.length != 3) {
            System.out.println("Usage: java MatrixDeterminant.java <n>");
            System.out.println("n: number of rows and columns of matrix");
        }
        var nRowsA = Integer.parseInt(args[0]);
        var nColsA = args.length == 3 ? Integer.parseInt(args[1]) : nRowsA;

        var A = new Matrix(nRowsA, nColsA);

        A.readMatrix();

        var result = calculateDeterminant(A);
        System.out.println(result);
    }

    static int calculateDeterminant(Matrix matrix) {
        var result = new Result();

        var detCalculator = new DeterminantCalculator(matrix, 1, result);
        var thread = new Thread(detCalculator);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return result.getResult();
    }

    static class DeterminantCalculator implements Runnable {
        Matrix matrix;
        Result result = new Result();
        int factor = 1;

        DeterminantCalculator(Matrix matrix, int factor, Result result) {
            if (matrix.getRowsSize() != matrix.getColumnsSize()) {
                throw new IllegalArgumentException("matrix must be square");
            }
            this.matrix = matrix;
            this.factor = factor;
            this.result = result;
        }

        DeterminantCalculator(Matrix matrix, int factor) {
            if (matrix.getRowsSize() != matrix.getColumnsSize()) {
                throw new IllegalArgumentException("matrix must be square");
            }
            this.matrix = matrix;
            this.factor = factor;
        }

        DeterminantCalculator(Matrix matrix) {
            if (matrix.getRowsSize() != matrix.getColumnsSize()) {
                throw new IllegalArgumentException("matrix must be square");
            }
            this.matrix = matrix;
        }

        public void run() {
            var result = 0;

            if (matrix.getRowsSize() == 1) {
                result = matrix.getElement(0, 0);
            } else if (matrix.getRowsSize() == 2) {
                result = matrix.getElement(0, 0) * matrix.getElement(1, 1)
                        - matrix.getElement(0, 1) * matrix.getElement(1, 0);
            } else {
                var id = ThreadLocalRandom.current().nextInt(1000, 9999);
                var group = new ThreadGroup("det" + id);
                var results = new ArrayList<Result>();
                for (int i = 0; i < matrix.getRowsSize(); i++) {
                    for (int j = 0; j < matrix.getColumnsSize(); j++) {
                        var result_ = new Result();
                        results.add(result_);

                        var factor = (i + j) % 2 == 0 ? 1 : -1;
                        factor *= matrix.getElement(i, j);

                        var subDet = new DeterminantCalculator(matrix.getSubmatrix(j), factor, result_);

                        var thread = new Thread(group, subDet);
                        thread.start();
                    }
                }

                while (group.activeCount() > 0) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                }

                result = results.stream().reduce(0, (x, y) -> x + y.getResult(), Integer::sum);
            }

            this.result.setResult(result).multiplyResult(factor);
        }

        public int getResult() {
            return result.getResult();
        }
    }

    private static class Result {
        private int result;

        public int getResult() {
            return result;
        }

        public Result setResult(int result) {
            this.result = result;
            return this;
        }

        public Result multiplyResult(int factor) {
            this.result *= factor;
            return this;
        }
    }
}
