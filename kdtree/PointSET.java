/* *****************************************************************************
 *  Name: Ian MacIntosh
 *  Date: May 19, 2020
 *  Description: Identify the points in a given search area (or alternatively, the closest point to a given point) from a square grid populated with points
 **************************************************************************** */


import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdDraw;

import java.util.Iterator;
import java.util.LinkedList;

public class PointSET {
    // construct an empty set of points
    private SET<Point2D> points;

    public PointSET() {
        // Make a new red-black BST and store it in `points`
        points = new SET<Point2D>();
    }

    // is the set empty?
    public boolean isEmpty() {
        // Return if the size of the points BST is 0
        return points.size() == 0;
    }

    // number of points in the set
    public int size() {
        // Return the size of the points BST
        return points.size();
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        // Time proportional to log N
        // Call SET library's add() routine
        points.add(p);
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        // Time proportional to log N
        // Use the Alg4 SET library's contains() routine
        return points.contains(p);
    }

    // draw all points to standard draw
    public void draw() {
        // Locate a draw thing to steal from -- maybe collinear points
        StdDraw.clear();
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);

        // Stolen from collinear points:

        // draw the points
        StdDraw.enableDoubleBuffering();
        // StdDraw.setXscale(0, 32768);
        // StdDraw.setYscale(0, 32768);
        for (Point2D p : points) {
            p.draw();
        }
        StdDraw.show();
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException();

        // Time proportional to N
        LinkedList<Point2D> locatedPoints = new LinkedList<Point2D>();

        // Use the Alg4 SET library's iterator() routine
        // Review every point and add it to an iterable list
        for (Iterator<Point2D> it = points.iterator(); it.hasNext(); ) {
            // I don't understand this syntax really
            Point2D thisPoint = it.next();

            // If:
            // This point's x is equal to or greater than the rect's min x
            // This point's x is equal to or less than the rect's max x
            // This point's y is equal to or greater than the rect's min y
            // This point's y is equal to or less than the rect's max y
            if (isInsideOf(thisPoint, rect)) {
                //      Add it to the locatedPoints
                locatedPoints.push(thisPoint);
            }
        }
        return locatedPoints;
    }

    // Tells if a given point is within a give range
    private boolean isInsideOf(Point2D thisPoint, RectHV rect) {
        if (thisPoint == null || rect == null) throw new IllegalArgumentException();
        // If:
        // This point's x is equal to or greater than the rect's min x
        // This point's x is equal to or less than the rect's max x
        // This point's y is equal to or greater than the rect's min y
        // This point's y is equal to or less than the rect's max y
        return thisPoint.x() >= rect.xmin() && thisPoint.x() <= rect.xmax() &&
                thisPoint.y() >= rect.ymin() && thisPoint.y() <= rect.ymax();
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        if (points.isEmpty()) return null;

        Point2D champion = null;
        // Time proportional to N
        // Review every point and compare it to a champion closest point, eventually returning the champ
        for (Iterator<Point2D> it = points.iterator(); it.hasNext(); ) {
            // I don't understand this syntax really
            Point2D thisPoint = it.next();

            // If champion is null or its distance is greater than the current point, update champion to this point
            if (champion == null || champion.distanceTo(p) > thisPoint.distanceTo(p)) {
                champion = thisPoint;
            }
        }

        return champion;
    }

    private static void reportTests(String testSubject, int testsPassed, int testsAttempted) {
        if (testsPassed == testsAttempted) {
            System.out.println("✅ " + testSubject + " passed " + testsPassed + "/" + testsAttempted
                                       + " tests");
        }
        else {
            System.out.println("🛑 " + testSubject + " passed " + testsPassed + "/" + testsAttempted
                                       + " tests");
            if (testsPassed > testsAttempted) System.out
                    .println("     Did you add a new test and forget to increment expected tests?");
        }
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
        String testSubject;
        String testDescription;
        int testsPassed;
        int testsInSuite;

        PointSET testPoints;
        Point2D testPoint1a = new Point2D(0.123, 0.456);
        Point2D testPoint1b = new Point2D(0.123, 0.456);
        Point2D testPoint2 = new Point2D(0.234, 0.567);
        RectHV fullRect = new RectHV(0, 0, 1, 1);

        /*
        The PointSET constructor can be called
         */
        testSubject = "PointSET constructor";
        testsPassed = 0;
        testsInSuite = 0;

        testDescription = "PointSET constructor can be called without throwing an error";
        testsInSuite++;
        try {
            testPoints = new PointSET();
            testsPassed++;
        }
        catch (IllegalArgumentException exception) {
            System.out.println("FAIL: " + testDescription);
        }

        reportTests(testSubject, testsPassed, testsInSuite);

        /*
        The isEmpty() routine should accurately report emptiness
         */
        testSubject = "isEmpty()";
        testsPassed = 0;
        testsInSuite = 0;

        testDescription = "isEmpty() reports an empty pointset is empty";
        testsInSuite++;

        testPoints = new PointSET();
        if (testPoints.isEmpty()) testsPassed++;
        else System.out.println("FAIL: " + testDescription);

        reportTests(testSubject, testsPassed, testsInSuite);

        /*
        The size() routine should accurately report size
         */
        testSubject = "size()";
        testsPassed = 0;
        testsInSuite = 0;

        testDescription = "size() reports 0 for an empty pointset";
        testsInSuite++;

        testPoints = new PointSET();
        if (testPoints.size() == 0) testsPassed++;
        else System.out.println("FAIL: " + testDescription);

        testDescription = "size() reports 2 after adding two points";
        testsInSuite++;

        testPoints.insert(testPoint1a);
        testPoints.insert(testPoint2);
        if (testPoints.size() == 2) testsPassed++;
        else System.out.println("FAIL: " + testDescription);

        reportTests(testSubject, testsPassed, testsInSuite);

        /*
        The insert() routine adds points properly
         */
        testSubject = "insert()";
        testsPassed = 0;
        testsInSuite = 0;
        testPoints = new PointSET();

        testDescription = "insert() throws IllegalArgumentException when called with null argument";
        testsInSuite++;
        try {
            testPoints.insert(null);
            System.out.println("FAIL: " + testDescription);
        }
        catch (IllegalArgumentException exception) {
            testsPassed++;
        }

        testDescription = "insert() can add a new point";
        testsInSuite++;

        testPoints.insert(testPoint1a);
        if (testPoints.size() == 1) testsPassed++;
        else System.out.println("FAIL: " + testDescription);

        testDescription = "insert() cannot add a duplicate point";
        testsInSuite++;

        testPoints.insert(testPoint1b);
        if (testPoints.size() == 1) testsPassed++;
        else System.out.println("FAIL: " + testDescription);

        reportTests(testSubject, testsPassed, testsInSuite);

        /*
        The closest() routine should return closest point
         */
        testSubject = "nearest()";
        testsPassed = 0;
        testsInSuite = 0;
        testPoints = new PointSET();

        testDescription
                = "nearest() throws IllegalArgumentException when called with null argument";
        testsInSuite++;
        try {
            testPoints.nearest(null);
            System.out.println("FAIL: " + testDescription);
        }
        catch (IllegalArgumentException exception) {
            testsPassed++;
        }

        testDescription = "nearest() reports null when it holds no points";
        testsInSuite++;

        testPoints = new PointSET();
        if (testPoints.nearest(testPoint2) == null) testsPassed++;
        else System.out.println("FAIL: " + testDescription);

        testDescription = "nearest() reports the nearest point";
        testsInSuite++;

        testPoints = new PointSET();
        testPoints.insert(testPoint1a);
        if (testPoints.nearest(testPoint2).equals(testPoint1a)) testsPassed++;
        else System.out.println("FAIL: " + testDescription);

        reportTests(testSubject, testsPassed, testsInSuite);

        /*
        The contains() routine should return closest point
         */
        testSubject = "contains()";
        testsPassed = 0;
        testsInSuite = 0;
        testPoints = new PointSET();

        testDescription
                = "contains() throws IllegalArgumentException when called with null argument";
        testsInSuite++;
        try {
            testPoints.contains(null);
            System.out.println("FAIL: " + testDescription);
        }
        catch (IllegalArgumentException exception) {
            testsPassed++;
        }

        testDescription = "contains() reports true when it holds the point";
        testsInSuite++;

        testPoints = new PointSET();
        testPoints.insert(testPoint1a);

        if (testPoints.contains(testPoint1a)) testsPassed++;
        else System.out.println("FAIL: " + testDescription);

        testDescription = "contains() reports false when it does not hold the point";
        testsInSuite++;

        testPoints = new PointSET();
        testPoints.insert(testPoint1a);

        if (!testPoints.contains(testPoint2)) testsPassed++;
        else System.out.println("FAIL: " + testDescription);

        reportTests(testSubject, testsPassed, testsInSuite);

        /*
        The range() routine should return located points
         */
        testSubject = "range()";
        testsPassed = 0;
        testsInSuite = 0;
        testPoints = new PointSET();

        testDescription
                = "range() throws IllegalArgumentException when called with null argument";
        testsInSuite++;
        try {
            testPoints.range(null);
            System.out.println("FAIL: " + testDescription);
        }
        catch (IllegalArgumentException exception) {
            testsPassed++;
        }

        testDescription = "range() returns an empty iterator when called on an empty set";
        testsInSuite++;

        int i = 0;
        for (Point2D point : testPoints.range(fullRect)) {
            i++;
        }

        if (i == 0) testsPassed++;
        else System.out.println("FAIL: " + testDescription);

        testDescription = "range() returns an iterator with one entry when matches one point";
        testsInSuite++;

        testPoints.insert(testPoint1a);

        i = 0;
        for (Point2D point : testPoints.range(fullRect)) {
            i++;
        }

        if (i == 1) testsPassed++;
        else System.out.println("FAIL: " + testDescription);

        reportTests(testSubject, testsPassed, testsInSuite);

        /*
        The draw() routine should display the points (NO WAY OF TESTING)
         */

        testPoints = new PointSET();
        testPoints.insert(testPoint1a);
        testPoints.insert(testPoint2);
        testPoints.draw();
    }
}
