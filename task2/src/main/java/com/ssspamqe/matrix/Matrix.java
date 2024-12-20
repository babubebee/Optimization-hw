package com.ssspamqe.matrix;

import com.ssspamqe.matrix.exceptions.DifferentMatrixDimensionsException;

public class Matrix {

    private final int rows;
    private final int columns;
    private final double[][] data;

    public Matrix(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.data = new double[rows][columns];
    }

    public Matrix plus(Matrix other) {
        if (this.rows != other.rows || this.columns != other.columns) {
            throw new DifferentMatrixDimensionsException();
        }

        Matrix result = new Matrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                var coordinate = Coordinate.of(i, j);
                double a = this.getAt(coordinate);
                double b = other.getAt(coordinate);
                result.setAt(coordinate, a + b);
            }
        }

        return result;
    }

    public Matrix minus(Matrix other) {
        if (this.rows != other.rows || this.columns != other.columns) {
            throw new DifferentMatrixDimensionsException();
        }

        Matrix result = new Matrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                var coordinate = Coordinate.of(i, j);
                double a = this.getAt(coordinate);
                double b = other.getAt(coordinate);
                result.setAt(coordinate, a - b);
            }
        }

        return result;
    }

    public Matrix multiply(Matrix other) {
        if (this.columns != other.rows) {
            throw new DifferentMatrixDimensionsException();
        }

        Matrix result = new Matrix(this.rows, other.columns);
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < other.columns; j++) {
                double sum = 0;
                for (int k = 0; k < this.columns; k++) {
                    sum += this.getAt(Coordinate.of(i, k)) * other.getAt(Coordinate.of(k, j));
                }
                result.setAt(Coordinate.of(i, j), sum);
            }
        }

        return result;
    }

    public Matrix multiply(double scalar) {
        Matrix result = new Matrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                var coordinate = Coordinate.of(i, j);
                result.setAt(coordinate, this.getAt(coordinate) * scalar);
            }
        }
        return result;
    }

    public static Matrix of(double[][] data) {
        int rows = data.length;
        int columns = data[0].length;
        Matrix matrix = new Matrix(rows, columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                matrix.setAt(Coordinate.of(i, j), data[i][j]);
            }
        }
        return matrix;
    }

    public void setAt(Coordinate coordinate, double value) {
        data[coordinate.row()][coordinate.column()] = value;
    }

    public double getAt(Coordinate coordinate) {
        return data[coordinate.row()][coordinate.column()];
    }

    public double[] getRow(int row) {
        return data[row];
    }

    public double[] getColumn(int column) {
        double[] result = new double[rows];
        for (int i = 0; i < rows; i++) {
            result[i] = data[i][column];
        }
        return result;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }
}
