package com.ssspamqe.matrix.util;

import com.ssspamqe.matrix.Coordinate;
import com.ssspamqe.matrix.Matrix;
import com.ssspamqe.matrix.exceptions.NotSquareMatrixException;
import com.ssspamqe.matrix.exceptions.SingularMatrixException;

public class MatrixInverser {

    public Matrix inverse(Matrix matrix) {
        int n = matrix.getRows();
        if (n != matrix.getColumns()) {
            throw new NotSquareMatrixException();
        }

        Matrix augmented = augmentWithIdentity(matrix);
        gaussianElimination(augmented);

        return extractInverse(augmented, n);
    }

    private Matrix augmentWithIdentity(Matrix matrix) {
        int n = matrix.getRows();
        Matrix augmented = new Matrix(n, 2 * n);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                augmented.setAt(new Coordinate(i, j), matrix.getAt(new Coordinate(i, j)));
            }
            augmented.setAt(new Coordinate(i, n + i), 1.0);
        }

        return augmented;
    }

    private void gaussianElimination(Matrix matrix) {
        int n = matrix.getRows();

        for (int i = 0; i < n; i++) {
            double pivot = matrix.getAt(new Coordinate(i, i));
            if (pivot == 0) {
                throw new SingularMatrixException();
            }

            for (int j = 0; j < 2 * n; j++) {
                matrix.setAt(new Coordinate(i, j), matrix.getAt(new Coordinate(i, j)) / pivot);
            }

            for (int k = 0; k < n; k++) {
                if (k != i) {
                    double factor = matrix.getAt(new Coordinate(k, i));
                    for (int j = 0; j < 2 * n; j++) {
                        matrix.setAt(new Coordinate(k, j), matrix.getAt(new Coordinate(k, j)) - factor * matrix.getAt(new Coordinate(i, j)));
                    }
                }
            }
        }
    }

    private Matrix extractInverse(Matrix augmented, int n) {
        Matrix inverse = new Matrix(n, n);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                inverse.setAt(new Coordinate(i, j), augmented.getAt(new Coordinate(i, n + j)));
            }
        }

        return inverse;
    }


}
