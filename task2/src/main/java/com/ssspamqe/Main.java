package com.ssspamqe;

import com.ssspamqe.matrix.Matrix;
import com.ssspamqe.test.ProblemType;
import com.ssspamqe.test.SingleTest;

public class Main {
    public static void main(String[] args) {
        var t1 = buildTest5();
        var res1 = t1.solve();
        t1.setIterations(20);
        t1.setAlpha(0.9);
        var res2 = t1.solve();
        System.out.println(res1);
        System.out.println(res2);
    }

    static SingleTest buildTest1() {
        var test = new SingleTest();
        test.setProblemType(ProblemType.MAXIMIZATION);
        test.setAlpha(0.5);
        test.setMatrixA(Matrix.of(new double[][]{
                {1, 2, 1, 0},
                {2, 1, 0, 1}
        }));
        test.setMatrixC(Matrix.of(new double[][]{
                {3},
                {4},
                {0},
                {0}
        }));
        test.setInitialPoint(new double[]{1, 1, 7, 9});
        return test;
    }

    static SingleTest buildTest2() {
        var test = new SingleTest();
        test.setProblemType(ProblemType.MAXIMIZATION);
        test.setAlpha(0.5);
        test.setMatrixA(Matrix.of(new double[][]{
                {1, 2, 1, 1, 0, 0},
                {4, 0, 1, 0, 1, 0},
                {2, 3, 0, 0, 0, 1}
        }));
        test.setMatrixC(Matrix.of(new double[][]{
                {3},
                {2},
                {1},
                {0},
                {0},
                {0}
        }));
        test.setInitialPoint(new double[]{1, 1, 1, 8, 11, 5});
        return test;
    }

    static SingleTest buildTest3() {
        var test = new SingleTest();
        test.setProblemType(ProblemType.MAXIMIZATION);
        test.setAlpha(0.5);
        test.setMatrixA(Matrix.of(new double[][]{
                {1, 1, 0, 1, 0, 0, 0},
                {2, 1, 1, 0, 1, 0, 0},
                {1, 0, 1, 0, 0, 1, 0},
                {0, 1, 2, 0, 0, 0, 1}
        }));
        test.setMatrixC(Matrix.of(new double[][]{
                {3},
                {4},
                {2},
                {0},
                {0},
                {0},
                {0}
        }));
        test.setInitialPoint(new double[]{1, 1, 1, 48, 76, 38, 27});
        return test;
    }

    static SingleTest buildTest4() {
        var test = new SingleTest();
        test.setProblemType(ProblemType.MAXIMIZATION);
        test.setAlpha(0.5);
        test.setMatrixA(Matrix.of(new double[][]{
                {1, 2, 1, 1, 0, 0, 0},
                {3, 2, 0, 0, 1, 0, 0},
                {0, 1, 3, 0, 0, 1, 0},
                {2, 0, 1, 0, 0, 0, 1}
        }));
        test.setMatrixC(Matrix.of(new double[][]{
                {4},
                {3},
                {5},
                {0},
                {0},
                {0},
                {0}
        }));
        test.setInitialPoint(new double[]{1, 1, 1, 96, 115, 56, 77});
        return test;
    }

    static SingleTest buildTest5() {
        var test = new SingleTest();
        test.setProblemType(ProblemType.MAXIMIZATION);
        test.setAlpha(0.5);
        test.setMatrixA(Matrix.of(new double[][]{
                {2, 3, 1, 1, 0, 0},
                {4, 1, 2, 0, 1, 0},
                {3, 2, 5, 0, 0, 1}
        }));
        test.setMatrixC(Matrix.of(new double[][]{
                {12},
                {15},
                {10},
                {0},
                {0},
                {0}
        }));
        test.setInitialPoint(new double[]{1, 1, 1, 24, 33, 50});
        return test;
    }
}