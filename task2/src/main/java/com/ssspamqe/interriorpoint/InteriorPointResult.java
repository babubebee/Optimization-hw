package com.ssspamqe.interriorpoint;

import com.ssspamqe.matrix.Matrix;

public record InteriorPointResult(
        double objectiveFunctionValue,
        double[] points
) {
    public static InteriorPointResult of(double value, double[] points) {
        return new InteriorPointResult(value, points);
    }

    public static InteriorPointResult of(double value, Matrix decisionVariables) {
        return new InteriorPointResult(value, decisionVariables.getColumn(0));
    }
}
