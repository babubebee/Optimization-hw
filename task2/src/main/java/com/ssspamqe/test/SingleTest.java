package com.ssspamqe.test;

import com.ssspamqe.interriorpoint.IterationResult;
import com.ssspamqe.interriorpoint.Solver;
import com.ssspamqe.matrix.Matrix;

public class SingleTest {
    private static final Solver SOLVER = new Solver();

    private double[] initialPoint;

    private ProblemType problemType;

    private Matrix MatrixA;

    private Matrix MatrixC;

    private double alpha;

    private Matrix MatrixB;

    private int iterations = 10;

    public IterationResult solve() {
        if (problemType == ProblemType.MINIMIZATION) {
            return calculateMinimization();
        }
        return calculateMaximization();
    }

    private IterationResult calculateMaximization() {
        return SOLVER.maximize(
                initialPoint,
                MatrixA,
                MatrixC,
                alpha,
                iterations
        );
    }

    private IterationResult calculateMinimization() {
        return SOLVER.minimize(
                initialPoint,
                MatrixA,
                MatrixC,
                alpha,
                iterations
        );
    }

    public double[] getInitialPoint() {
        return initialPoint;
    }

    public void setInitialPoint(double[] initialPoint) {
        this.initialPoint = initialPoint;
    }

    public ProblemType getProblemType() {
        return problemType;
    }

    public void setProblemType(ProblemType problemType) {
        this.problemType = problemType;
    }

    public Matrix getMatrixA() {
        return MatrixA;
    }

    public void setMatrixA(Matrix matrixA) {
        MatrixA = matrixA;
    }

    public Matrix getMatrixC() {
        return MatrixC;
    }

    public void setMatrixC(Matrix matrixC) {
        MatrixC = matrixC;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public Matrix getMatrixB() {
        return MatrixB;
    }

    public void setMatrixB(Matrix matrixB) {
        MatrixB = matrixB;
    }
}
