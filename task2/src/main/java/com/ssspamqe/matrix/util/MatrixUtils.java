package com.ssspamqe.matrix.util;

import com.ssspamqe.matrix.Coordinate;
import com.ssspamqe.matrix.Matrix;

public class MatrixUtils {

    private static final MatrixInverser INVERSER = new MatrixInverser();

    public static Matrix inverse(Matrix matrix) {
        return INVERSER.inverse(matrix);
    }

    public static Matrix transpose(Matrix matrix) {
        var result = new Matrix(matrix.getColumns(), matrix.getRows());
        for (int i = 0; i < matrix.getRows(); i++) {
            for (int j = 0; j < matrix.getColumns(); j++) {
                result.setAt(Coordinate.of(j, i), matrix.getAt(Coordinate.of(i, j)));
            }
        }
        return result;
    }

    public static Matrix toDiagonalMatrix(double[] array) {
        var matrix = new Matrix(array.length, array.length);
        for (int i = 0; i < matrix.getColumns(); i++) {
            matrix.setAt(Coordinate.of(i, i), array[i]);
        }
        return matrix;
    }

    public static Matrix buildIdentityMatrixOfSize(int size) {
        var matrix = new Matrix(size, size);
        for (int i = 0; i < size; i++) {
            matrix.setAt(Coordinate.of(i, i), 1.0);
        }
        return matrix;
    }

    public static Matrix toColumnVector(double[] data) {
        var matrix = new Matrix(data.length, 1);
        for (int i = 0; i < data.length; i++) {
            matrix.setAt(Coordinate.of(i, 0), data[i]);
        }
        return matrix;
    }

}
