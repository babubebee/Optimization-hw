package com.ssspamqe.interriorpoint;

import com.ssspamqe.matrix.Matrix;

public record IterationResult(
        double[] point,
        double[] decisionVariables
) {
    public static IterationResult of(double[] point, double[] decisionVariables) {
        return new IterationResult(point, decisionVariables);
    }

    public static IterationResult of(Matrix point, Matrix decisionVariables) {
        return new IterationResult(point.getColumn(0), decisionVariables.getColumn(0));
    }
}
