package com.ssspamqe.interriorpoint;

import com.ssspamqe.interriorpoint.exceptions.InvalidCpMatrixException;
import com.ssspamqe.matrix.Coordinate;
import com.ssspamqe.matrix.Matrix;
import com.ssspamqe.matrix.util.MatrixUtils;

public class Solver {

    public IterationResult maximize(double[] initialPoint, Matrix A, Matrix C, double alpha, int iterations) {
        var solution = MatrixUtils.toColumnVector(initialPoint);
        IterationResult lastIterationResult = null;
        for (int i = 0; i < iterations; i++) {
            lastIterationResult = proceedMaximizationIteration(solution, A, C, alpha);
            solution = MatrixUtils.toColumnVector(lastIterationResult.point());
        }
        return lastIterationResult;
    }

    public IterationResult minimize(double[] initialPoint, Matrix A, Matrix C, double alpha, int iterations) {
        var solution = MatrixUtils.toColumnVector(initialPoint);
        IterationResult lastIterationResult = null;
        for (int i = 0; i < iterations; i++) {
            lastIterationResult = proceedMinimizationIteration(solution, A, C, alpha);
            solution = MatrixUtils.toColumnVector(lastIterationResult.point());
        }
        return lastIterationResult;
    }

    private IterationResult proceedMaximizationIteration(Matrix solution, Matrix A, Matrix C, double alpha) {
        var D = MatrixUtils.toDiagonalMatrix(solution.getColumn(0));
        var newA = A.multiply(D);
        var newC = D.multiply(C);
        var P = calculatePMatrix(newA);
        var cp = P.multiply(newC);
        var decisionVariables = calculateNewXForMaximization(alpha, cp);
        var newSolution = D.multiply(decisionVariables);
        return IterationResult.of(newSolution, decisionVariables);
    }

    private Matrix calculatePMatrix(Matrix a) {
        var idenity = MatrixUtils.buildIdentityMatrixOfSize(a.getColumns());
        var transposed = MatrixUtils.transpose(a);
        var inversed = MatrixUtils.inverse(a.multiply(transposed));
        var multiplication = transposed.multiply(inversed).multiply(a);
        return idenity.minus(multiplication);
    }

    private Matrix calculateNewXForMaximization(double alpha, Matrix cp) {
        var columnVectorOfOnes = buildColumnVectorOfOnes(cp.getRows());
        double v = getVForMaximization(cp);
        var buf = cp.multiply(alpha / v);
        return columnVectorOfOnes.plus(buf);
    }

    private double getVForMaximization(Matrix cp) {
        int n = cp.getRows();
        double mx = 0;
        for (int i = 0; i < n; i++) {
            var coordinate = Coordinate.of(i, 0);
            var value = cp.getAt(coordinate);
            if (value < 0) {
                mx = Math.max(mx, Math.abs(value));
            }
        }
        if (mx == 0) {
            throw new InvalidCpMatrixException("Matrix is not feasible");
        }
        return mx;
    }

    private IterationResult proceedMinimizationIteration(Matrix solution, Matrix A, Matrix C, double alpha) {
        var D = MatrixUtils.toDiagonalMatrix(solution.getColumn(0));
        var newA = A.multiply(D);
        var newC = D.multiply(C);
        var P = calculatePMatrix(newA);
        var cp = P.multiply(newC);
        var decisionVariables = calculateNewXForMinimization(alpha, cp);
        var newSolution = D.multiply(decisionVariables);
        return IterationResult.of(newSolution, decisionVariables);
    }

    private Matrix calculateNewXForMinimization(double alpha, Matrix cp) {
        var columnVectorOfOnes = buildColumnVectorOfOnes(cp.getRows());
        double v = getVForMinimization(cp);
        var buf = cp.multiply(alpha / v);
        return columnVectorOfOnes.plus(buf);
    }

    private double getVForMinimization(Matrix cp) {
        int n = cp.getRows();
        double mx = Double.MAX_VALUE;
        for (int i = 0; i < n; i++) {
            var coordinate = Coordinate.of(i, 0);
            var value = cp.getAt(coordinate);
            if (value < 0) {
                mx = Math.max(mx, Math.abs(value));
            }
        }

        if (mx == 0) {
            throw new InvalidCpMatrixException("Matrix is not feasible");
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
