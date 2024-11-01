package com.ssspamqe.matrix.exceptions;

public class NotSquareMatrixException extends MatrixException {
    public NotSquareMatrixException(String message) {
        super(message);
    }

    public NotSquareMatrixException() {
        super("The matrix is not square");
    }
}
