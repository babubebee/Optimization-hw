package com.ssspamqe.matrix.exceptions;

public class SingularMatrixException extends MatrixException {
    public SingularMatrixException(String message) {
        super(message);
    }

    public SingularMatrixException() {
        super("The matrix is singular");
    }
}
