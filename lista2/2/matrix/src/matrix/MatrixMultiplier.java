package matrix;

public class MatrixMultiplier {

    public static void main(String[] args) {
        if (args.length != 1 && args.length != 3) {
            System.out.println("Usage: java MatrixMultiplier.java <x1> [<x2> <x3>]");
            System.out.println("x1: number of rows and columns of both matrices");
            System.out.println(
                    "x1 x2 x3: number of rows of first matrix, number of columns of first and number of rows of second, number of columns of second, respectively");
            return;
        }
        var nRowsA = Integer.parseInt(args[0]);
        var nColsA = args.length == 3 ? Integer.parseInt(args[1]) : nRowsA;
        var nRowsB = nColsA;
        var nColsB = args.length == 3 ? Integer.parseInt(args[2]) : nRowsA;

        var A = new Matrix(nRowsA, nColsA);
        var B = new Matrix(nRowsB, nColsB);

        A.readMatrix();
        B.readMatrix();

        var result = multiplyMatrices(A, B);
        result.printMatrix();
    }

    static Matrix multiplyMatrices(Matrix A, Matrix B) {
        if (A.getColumnsSize() != B.getRowsSize()) {
            throw new IllegalArgumentException("A must have the same number of columns as number of rows in B");
        }

        var result = new Matrix(A.getRowsSize(), B.getColumnsSize());

        var multipliersGroup = new ThreadGroup("multipliers");
        for (int i = 0; i < result.getRowsSize(); i++) {
            var row = A.getRow(i);
            for (int j = 0; j < result.getColumnsSize(); j++) {
                var col = B.getColumn(j);
                var multiplier = new VectorMultiplier(row, col, result, i, j);
                var thread = new Thread(multipliersGroup, multiplier);
                thread.start();
            }
        }

        while (multipliersGroup.activeCount() > 0) {
            try {
                Thread.sleep(50);
            }
            catch (InterruptedException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        return result;
    }

    static class VectorMultiplier implements Runnable {
        private final int[] firstVector;
        private final int[] secondVector;
        private final Matrix matrix;
        private final int i;
        private final int j;

        VectorMultiplier(int[] firstVector, int[] secondVector, Matrix result, int i, int j) {
            if (firstVector.length != secondVector.length) {
                throw new IllegalArgumentException("Vectors must have the same length");
            }

            this.firstVector = firstVector;
            this.secondVector = secondVector;
            this.matrix = result;
            this.i = i;
            this.j = j;
        }

        public void run() {
            var result = 0;
            for (int i = 0; i < firstVector.length; i++) {
                result += firstVector[i] * secondVector[i];
            }

            matrix.setElement(i, j, result);
        }
    }
}
