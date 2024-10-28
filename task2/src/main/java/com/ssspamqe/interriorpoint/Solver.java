package com.ssspamqe.interriorpoint;

import com.ssspamqe.matrix.Coordinate;
import com.ssspamqe.matrix.Matrix;
import com.ssspamqe.matrix.util.MatrixUtils;

public class Solver {

    public double[] solve(double[] initialPoint, Matrix A, Matrix C, double alpha, int iterations) {
        var solution = MatrixUtils.toColumnVector(initialPoint);

        for (int i = 0; i < iterations; i++) {
            solution = proceedIteration(solution, A, C, alpha);
        }

        return solution.getColumn(0);
    }

    private Matrix proceedIteration(Matrix solution, Matrix A, Matrix C, double alpha) {
        var D = MatrixUtils.toDiagonalMatrix(solution.getColumn(0));
        var newA = A.multiply(D);
        var newC = D.multiply(C);
        var P = calculatePMatrix(newA);
        var cp = P.multiply(newC);
        var newX = calculateNewX(alpha, cp);
        return D.multiply(newX);

    }

    private Matrix calculatePMatrix(Matrix a) {
        var idenity = MatrixUtils.buildIdentityMatrixOfSize(a.getRows());
        var transposed = MatrixUtils.transpose(a);
        var inversed = MatrixUtils.inverse(a.multiply(transposed));
        var multiplication = transposed.multiply(inversed).multiply(a);
        return idenity.minus(multiplication);
    }

    private Matrix calculateNewX(double alpha, Matrix cp) {
        var columnVectorOfOnes = buildColumnVectorOfOnes(cp.getRows());
        double v = getV(cp);
        var buf = cp.multiply(alpha / v);
        return columnVectorOfOnes.plus(buf);
    }

    private double getV(Matrix cp) {
        int n = cp.getRows();
        double mx = Math.abs(cp.getAt(Coordinate.of(0, 0)));
        for (int i = 0; i < n; i++) {
            double abs = Math.abs(cp.getAt(Coordinate.of(i, 0)));
            mx = Math.max(mx, abs);
        }
        return mx;
    }

    private Matrix buildColumnVectorOfOnes(int size) {
        var result = new Matrix(size, 1);
        for (int i = 0; i < size; i++) {
            result.setAt(Coordinate.of(i, 0), 1.0);
        }
        return result;
    }

}
