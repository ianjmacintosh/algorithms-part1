/* *****************************************************************************
 *  Name: Ian MacIntosh
 *  Date: May 20, 2020
 *  Description: Identify the points in a given search area (or alternatively, the closest point to a given point) from a square grid populated with points
 **************************************************************************** */


import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import java.util.LinkedList;

public class KdTree {
    private static final boolean RED = true;
    private static final boolean BLUE = false;

    // construct an empty set of points
    private int size; // Size of tree
    private Node root; // The root of our tree
    private LinkedList<Point2D> locatedNodes = new LinkedList<Point2D>();

    private class Node {
        private Point2D point; // Point ("value")
        private boolean color; // true = red, false = blue
        private Node left, right; // The left and right children

        Node(Point2D p) {
            point = p;
        }

        public boolean isRed() {
            return color == RED;
        }
    }

    public KdTree() {
        // Make a new red-black BST and store it in `points`
        size = 0;
    }

    // is the set empty?
    public boolean isEmpty() {
        // Return if the size of the points BST is 0
        return size == 0;
    }

    // number of points in the set
    public int size() {
        // Return the size of the points BST
        return size;
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException();

        Node newNode = new Node(p);
        Node currentNode = root;
        // Time proportional to log N
        // Follow code on p.439

        if (root == null) {
            root = new Node(p);
            size++;
        }
        else {
            int cmp = 1;
            while (true) {
                if (currentNode.point.equals(p)) {
                    break;
                }
                // Decide if we look to the left or right branch (depends on color)
                if (currentNode.isRed()) {
                    newNode.color = false;
                    if (p.x() < currentNode.point.x()) cmp = -1; // Go left
                }
                else {
                    newNode.color = true;
                    if (p.y() < currentNode.point.y()) cmp = -1; // Go left
                }
                if (cmp < 0) {
                    if (currentNode.left == null) {
                        currentNode.left = newNode;
                        size++;
                        break;
                    }
                    else {
                        currentNode = currentNode.left;
                    }
                }
                else {
                    if (currentNode.right == null) {
                        currentNode.right = newNode;
                        size++;
                        break;
                    }
                    else {
                        currentNode = currentNode.right;
                    }
                }
            }
        }
        // points.add(p);
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        // Time proportional to log N
        if (p == null) throw new IllegalArgumentException();
        if (size == 0) return false;

        int cmp = 1;
        Node currentNode = root;

        while (true) {
            if (currentNode == null) return false;
            if (currentNode.point.equals(p)) return true;

            // Decide if we look to the left or right branch (depends on color)
            if (currentNode.isRed()) {
                if (p.x() < currentNode.point.x()) cmp = -1; // Go left
            }
            else {
                if (p.y() < currentNode.point.y()) cmp = -1; // Go left
            }

            if (cmp > 0) currentNode = currentNode.right;
            if (cmp < 0) currentNode = currentNode.left;
        }
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

        // TODO Uncomment this
        // for (Point2D p : points) {
        //     p.draw();
        // }
        StdDraw.show();
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException();
        // Slides say:
        /*
        // Check if point in node lies in given rectangle.
        // Recursively search left/bottom (if any could fall in rectangle).
        // Recursively search right/top (if any could fall in rectangle)
         */

        // My interpretation:
        // We're going to find all points inside rect
        // First check if rectangle contains the node at the root
        // If not, which side of the current point is it on?
        // Check the next child node in that direction to see if it's inside rect
        // If the rectangle intersects the splitting line, search both subtrees
        // Search the left subtree first:
        // Does the rect contain the left child point?
        // If no, what side is the rect on? Search that side
        // Search the left subtree, does the rect contain the left child point?
        // Add it to the list if yes
        // Does the rect intersect the split? If so, you have to search both sides
        // In the example, both sides are empty, so we go back up to the last point where
        // If the line intersects the rectangle, you will have to search both sides
        locatedNodes.clear();
        getWithin(root, rect);
        return locatedNodes;
    }

    private void getWithin(Node currentNode, RectHV rect) {
        // Return all nodes within a given range
        // Return value associated with key in the subtree rooted at x;
        // return null if key not present in subtree rooted at x.

        // Is "this node" null? Then return
        if (currentNode == null) return;

        // Does the rectangle intersect the split line for this node?
        if (intersects(currentNode, rect)) {
            // If YES:
            //      Does the rectangle wrap the point?
            //      If YES:
            //          Add the point to the list
            if (isInsideOf(currentNode.point, rect)) locatedNodes.push(currentNode.point);

            //      Either way:
            //          Call this routine again recursively on both children
            getWithin(currentNode.left, rect);
            getWithin(currentNode.right, rect);
        }
        else {
            // If NO:
            //      Call this routine again on the appropriate child node
            int cmp = 1;
            // Decide if we look to the left or right branch (depends on color)
            if (currentNode.isRed()) {
                if (rect.xmin() < currentNode.point.x()) cmp = -1; // Go left
            }
            else {
                if (rect.ymin() < currentNode.point.y()) cmp = -1; // Go left
            }

            if (cmp < 0) getWithin(currentNode.left, rect);
            if (cmp > 0) getWithin(currentNode.right, rect);
        }
    }

    // Tells if a given point is within a give range
    private boolean intersects(Node thisNode, RectHV rect) {
        if (thisNode == null || rect == null) throw new IllegalArgumentException();
        Point2D thisPoint = thisNode.point;
        // If:
        // This point's x is equal to or greater than the rect's min x
        // This point's x is equal to or less than the rect's max x
        // This point's y is equal to or greater than the rect's min y
        // This point's y is equal to or less than the rect's max y
        if (thisNode.isRed()) {
            return thisPoint.x() >= rect.xmin() && thisPoint.x() <= rect.xmax();
        }
        else {
            return thisPoint.y() >= rect.ymin() && thisPoint.y() <= rect.ymax();
        }
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
    // public Point2D nearest(Point2D p) {
    //     if (p == null) throw new IllegalArgumentException();
    //     for (Iterator<Point2D> it = getWithin().iterator(); it.hasNext(); ) {
    //         if (points.isEmpty()) return null;
    //
    //         Point2D champion = null;
    //         // Time proportional to N
    //         // Review every point and compare it to a champion closest point, eventually returning the champ
    //         // I don't understand this syntax really
    //         Point2D thisPoint = it.next();
    //
    //         // If champion is null or its distance is greater than the current point, update champion to this point
    //         if (champion == null || champion.distanceTo(p) > thisPoint.distanceTo(p)) {
    //             champion = thisPoint;
    //         }
    //     }
    //
    //     return p;
    // }

    private static void reportTests(String testSubject, int testsPassed, int testsAttempted) {
        if (testsPassed == testsAttempted) {
            System.out.println(
                    "✅ " + testSubject + " passed " + testsPassed + "/" + testsAttempted
                            + " tests");
        }
        else {
            System.out.println(
                    "🛑 " + testSubject + " passed " + testsPassed + "/" + testsAttempted
                            + " tests");
            if (testsPassed > testsAttempted) System.out
                    .println(
                            "     Did you add a new test and forget to increment expected tests?");
        }
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
        String testSubject;
        String testDescription;
        int testsPassed;
        int testsInSuite;

        KdTree testPoints;
        Point2D testPoint1a = new Point2D(0.123, 0.456);
        Point2D testPoint1b = new Point2D(0.123, 0.456);
        Point2D testPoint2 = new Point2D(0.234, 0.567);
        RectHV fullRect = new RectHV(0, 0, 1, 1);
        RectHV bottomHalfRect = new RectHV(0, 0, 1, 0.5);
        RectHV topHalfRect = new RectHV(0.5, 0.5, 1, 1);

        /*
        The PointSET constructor can be called
         */
        testSubject = "KdTree constructor";
        testsPassed = 0;
        testsInSuite = 0;

        testDescription = "KdTree constructor can be called without throwing an error";
        testsInSuite++;
        try {
            testPoints = new KdTree();
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

        testPoints = new KdTree();
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

        testPoints = new KdTree();
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
        testPoints = new KdTree();

        testDescription
                = "insert() throws IllegalArgumentException when called with null argument";
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
        The nearest() routine should return closest point
         */
        // testSubject = "nearest()";
        // testsPassed = 0;
        // testsInSuite = 0;
        // testPoints = new KdTree();
        //
        // testDescription
        //         = "nearest() throws IllegalArgumentException when called with null argument";
        // testsInSuite++;
        // try {
        //     testPoints.nearest(null);
        //     System.out.println("FAIL: " + testDescription);
        // }
        // catch (IllegalArgumentException exception) {
        //     testsPassed++;
        // }
        //
        // testDescription = "nearest() reports null when it holds no points";
        // testsInSuite++;
        //
        // testPoints = new KdTree();
        // if (testPoints.nearest(testPoint2) == null) testsPassed++;
        // else System.out.println("FAIL: " + testDescription);
        //
        // testDescription = "nearest() reports the nearest point";
        // testsInSuite++;
        //
        // testPoints = new KdTree();
        // testPoints.insert(testPoint1a);
        // if (testPoints.nearest(testPoint2).equals(testPoint1a)) testsPassed++;
        // else System.out.println("FAIL: " + testDescription);
        //
        // reportTests(testSubject, testsPassed, testsInSuite);

        /*
        The contains() routine should describe if a tree has a given point
         */
        testSubject = "contains()";
        testsPassed = 0;
        testsInSuite = 0;
        testPoints = new KdTree();

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

        testPoints = new KdTree();
        testPoints.insert(testPoint1a);

        if (testPoints.contains(testPoint1a)) testsPassed++;
        else System.out.println("FAIL: " + testDescription);

        testDescription = "contains() reports false when it does not hold the point";
        testsInSuite++;

        testPoints = new KdTree();
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
        testPoints = new KdTree();

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

        testDescription = "range() finds a point in the bottom half";
        testsInSuite++;

        testPoints.insert(testPoint1a);

        i = 0;
        for (Point2D point : testPoints.range(bottomHalfRect)) {
            i++;
        }

        if (i == 1) testsPassed++;
        else System.out.println("FAIL: " + testDescription);

        testDescription = "range() doesn't find a point in the top half";
        testsInSuite++;

        testPoints.insert(testPoint1a);

        i = 0;
        for (Point2D point : testPoints.range(topHalfRect)) {
            i++;
        }

        if (i == 0) testsPassed++;
        else System.out.println("FAIL: " + testDescription);

        reportTests(testSubject, testsPassed, testsInSuite);

        /*
        The draw() routine should display the points (NO WAY OF TESTING)
         */

        testPoints = new KdTree();
        testPoints.insert(testPoint1a);
        testPoints.insert(testPoint2);
        testPoints.draw();
    }
}
