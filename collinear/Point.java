/******************************************************************************
 *  Compilation:  javac Point.java
 *  Execution:    java Point
 *  Dependencies: none
 *
 *  An immutable data type for points in the plane.
 *  For use on Coursera, Algorithms Part I programming assignment.
 *
 ******************************************************************************/

import edu.princeton.cs.algs4.StdDraw;

import java.util.Comparator;

public class Point implements Comparable<Point> {
    private final int x;     // x-coordinate of this point
    private final int y;     // y-coordinate of this point

    /**
     * Initializes a new point.
     *
     * @param x the <em>x</em>-coordinate of the point
     * @param y the <em>y</em>-coordinate of the point
     */
    public Point(int x, int y) {
        /* DO NOT MODIFY */
        this.x = x;
        this.y = y;
    }

    /**
     * Draws this point to standard draw.
     */
    public void draw() {
        /* DO NOT MODIFY */
        StdDraw.point(x, y);
    }

    /**
     * Draws the line segment between this point and the specified point
     * to standard draw.
     *
     * @param that the other point
     */
    public void drawTo(Point that) {
        /* DO NOT MODIFY */
        StdDraw.line(this.x, this.y, that.x, that.y);
    }

    /**
     * Returns the slope between this point and the specified point.
     * Formally, if the two points are (x0, y0) and (x1, y1), then the slope
     * is (y1 - y0) / (x1 - x0). For completeness, the slope is defined to be
     * +0.0 if the line segment connecting the two points is horizontal;
     * Double.POSITIVE_INFINITY if the line segment is vertical;
     * and Double.NEGATIVE_INFINITY if (x0, y0) and (x1, y1) are equal.
     *
     * @param that the other point
     * @return the slope between this point and the specified point
     */
    public double slopeTo(Point that) {
        /* YOUR CODE HERE */
        // Bail if given bad argument:
        //   * null
        //   * Not a point

        double x0 = this.x;
        double y0 = this.y;
        double x1 = that.x;
        double y1 = that.y;

        if (y0 == y1) {
            if (x0 == x1) return Double.NEGATIVE_INFINITY;
            else return 0.0;
        }
        else if (x0 == x1) return Double.POSITIVE_INFINITY;

        return (y1 - y0) / (x1 - x0);
    }

    /**
     * Compares two points by y-coordinate, breaking ties by x-coordinate.
     * Formally, the invoking point (x0, y0) is less than the argument point
     * (x1, y1) if and only if either y0 < y1 or if y0 = y1 and x0 < x1.
     *
     * @param that the other point
     * @return the value <tt>0</tt> if this point is equal to the argument
     * point (x0 = x1 and y0 = y1);
     * a negative integer if this point is less than the argument
     * point; and a positive integer if this point is greater than the
     * argument point
     */
    public int compareTo(Point that) {
        // The spec doesn't say how to handle bad inputs?
        // Bail if given bad argument:
        //   * null
        //   * Not a point

        int yDiff = this.y - that.y;

        if (yDiff != 0) return yDiff;
        else {
            int xDiff = this.x - that.x;
            if (xDiff != 0) return xDiff;
        }

        return 0;
    }

    /**
     * Compares two points by the slope they make with this point.
     * The slope is defined as in the slopeTo() method.
     *
     * @return the Comparator that defines this ordering on points
     */
    public Comparator<Point> slopeOrder() {
        return new SlopeOrder();
    }

    private class SlopeOrder implements Comparator<Point> {
        public int compare(Point p1, Point p2) {
            double s1 = slopeTo(p1);
            double s2 = slopeTo(p2);
            if (s1 > s2) return 1;
            else if (s1 < s2) return -1;
            return 0;
        }
    }

    /**
     * Returns a string representation of this point.
     * This method is provide for debugging;
     * your program should not rely on the format of the string representation.
     *
     * @return a string representation of this point
     */
    public String toString() {
        /* DO NOT MODIFY */
        return "(" + x + ", " + y + ")";
    }

    /**
     * Unit tests the Point data type.
     */
    public static void main(String[] args) {
        String testDescription;
        int testsExpected;
        int testsPassed;

        // Sample points
        Point testPoint00 = new Point(0, 0);
        Point testPoint00a = new Point(0, 0); // To suppress errors
        Point testPoint01 = new Point(0, 1);
        Point testPoint10 = new Point(1, 0);
        Point testPoint11 = new Point(1, 1);
        Point testPoint22 = new Point(2, 2);

        /*
        The compareTo() method should compare points by their y-coordinates,
        breaking ties by their x-coordinates. Formally, the invoking point (x0,
        y0) is less than the argument point (x1, y1) if and only if either
        y0 < y1 or if y0 = y1 and x0 < x1.
        */
        testsPassed = 0;

        testDescription = "Returns 0 for identical points";
        if (testPoint00.compareTo(testPoint00a) == 0) {
            testsPassed++;
        }
        else {
            throw new Error("FAIL: " + testDescription);
        }

        testDescription = "Returns negative for comparison to same x-axis, greater y-axis";
        if (testPoint00.compareTo(testPoint01) < 0) {
            testsPassed++;
        }
        else {
            throw new Error("FAIL: " + testDescription);
        }

        testDescription = "Returns negative for comparison to lesser x-axis, lesser y-axis";
        if (testPoint11.compareTo(testPoint00) > 0) {
            testsPassed++;
        }
        else {
            throw new Error("FAIL: " + testDescription);
        }

        testDescription = "Returns negative for comparison to greater x-axis, same y-axis";
        if (testPoint00.compareTo(testPoint10) < 0) {
            testsPassed++;
        }
        else {
            throw new Error("FAIL: " + testDescription);
        }

        testDescription = "Returns positive for comparison to same x-axis, lesser y-axis";
        if (testPoint11.compareTo(testPoint10) > 0) {
            testsPassed++;
        }
        else {
            throw new Error("FAIL: " + testDescription);
        }

        testDescription = "Returns positive for comparison to lesser x-axis, greater y-axis";
        if (testPoint01.compareTo(testPoint10) > 0) {
            testsPassed++;
        }
        else {
            throw new Error("FAIL: " + testDescription);
        }

        testDescription = "Returns positive for comparison to lesser x-axis, same y-axis";
        if (testPoint10.compareTo(testPoint00) > 0) {
            testsPassed++;
        }
        else {
            throw new Error("FAIL: " + testDescription);
        }

        testsExpected = 7;
        if (testsPassed == testsExpected) {
            System.out.print("All " + testsExpected + " tests passed -- ");
            System.out.println("compareTo() compares points by y-axis, then x-axis");
        }
        else if (testsPassed > testsExpected) {
            throw new Error("Unaccounted test in " + testDescription + " suite");
        }

        /*
        The slopeTo() method should return the slope between the invoking point
        (x0, y0) and the argument point (x1, y1), which is given by the formula
        (y1 − y0) / (x1 − x0). Treat the slope of a horizontal line segment as
        positive zero; treat the slope of a vertical line segment as positive
        infinity; treat the slope of a degenerate line segment (between a point
        and itself) as negative infinity.
         */
        testsPassed = 0;

        testDescription = "Horizontal lines segments return positive 0";
        if (testPoint10.slopeTo(testPoint00) == +0.0) {
            testsPassed++;
        }
        else {
            throw new Error("FAIL: " + testDescription);
        }

        testDescription = "Vertical lines segments return positive infinity";
        if (testPoint00.slopeTo(testPoint01) == Double.POSITIVE_INFINITY) {
            testsPassed++;
        }
        else {
            throw new Error("FAIL: " + testDescription);
        }

        testDescription = "Degenerate lines segments return negative infinity";
        if (testPoint00.slopeTo(testPoint00a) == Double.NEGATIVE_INFINITY) {
            testsPassed++;
        }
        else {
            throw new Error("FAIL: " + testDescription);
        }

        testDescription = "The slope from 0,0 to 1,1 equals the slope from 0,0 to 2,2";
        if (testPoint00.slopeTo(testPoint11) == testPoint00.slopeTo(testPoint22)) {
            testsPassed++;
        }
        else {
            throw new Error("FAIL: " + testDescription);
        }

        testDescription = "The slope from 0,0 to 1,1 does not equal the slope from 0,0 to 0,1";
        if (testPoint00.slopeTo(testPoint11) != testPoint00.slopeTo(testPoint01)) {
            testsPassed++;
        }
        else {
            System.out.println(testPoint00.slopeTo(testPoint11));
            System.out.println(testPoint00.slopeTo(testPoint01));
            throw new Error("FAIL: " + testDescription);
        }

        testsExpected = 5;
        if (testsPassed == testsExpected) {
            System.out.print("All " + testsExpected + " tests passed -- ");
            System.out.println("slopeTo() meets spec");
        }
        else if (testsPassed > testsExpected) {
            throw new Error("Unaccounted test in " + testDescription + " suite");
        }

        /*
        The slopeOrder() method should return a comparator that compares its
        two argument points by the slopes they make with the invoking point
        (x0, y0). Formally, the point (x1, y1) is less than the point (x2, y2)
        if and only if the slope (y1 − y0) / (x1 − x0) is less than the slope
        (y2 − y0) / (x2 − x0). Treat horizontal, vertical, and degenerate line
        segments as in the slopeTo() method.
        */
        // testsPassed = 0;
        //
        // testDescription = "Horizontal lines segments return positive 0";
        // if (testPoint00(testPoint00.slopeOrder()) {
        //     testsPassed++;
        // }
        // else{
        //     throw new Error("FAIL: " + testDescription);
        // }

        /*
        Do not override the equals() or hashCode() methods.
         */
    }
}
