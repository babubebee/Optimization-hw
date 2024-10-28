package com.ssspamqe.matrix.exceptions;

public class DifferentMatrixDimensionsException extends MatrixException {
    public DifferentMatrixDimensionsException(String message) {
        super(message);
    }

    public DifferentMatrixDimensionsException() {
        super("The matrices have different measurements");
    }
}
