/* *****************************************************************************
 *  Name: Ian MacIntosh
 *  Date: May 2, 2020
 *  Description: Exercise for Algorithms, Part I (Princeton University)
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;
import java.util.LinkedList;

public class FastCollinearPoints {
    private int n; // Number of straight lines
    private LineSegment[] a; // Collection of straight lines
    private final boolean DEBUG = true;

    // finds all line segments containing 4 or more points
    public FastCollinearPoints(Point[] points) {
        // Throw an IllegalArgumentException if:
        // * the argument to the constructor is null
        if (points == null) {
            throw new IllegalArgumentException();
        }

        LinkedList<LineSegment> b = new LinkedList<>();
        LinkedList<Point> bHeads = new LinkedList<>();
        LinkedList<Point> bTails = new LinkedList<>();
        n = 0;
        for (int i = 0, j = points.length; i < j; i++) {
            Point thisPoint = points[i];
            // * any point in the array is null
            if (thisPoint == null) throw new IllegalArgumentException();

            // * the argument to the constructor contains a repeated point.
            points[i] = null;
            for (int k = 0; k < j; k++) {
                if (thisPoint.equals(points[k])) throw new IllegalArgumentException();
            }
            points[i] = thisPoint;
        }

        // Review all possible tails and see which tail is farthest from the head on the same slope
        // Iterate over each point in points
        for (int i = 0, j = points.length; i < j; i++) {
            Point origin = points[i];

            // * Sort points by slope to current origin
            Point[] pointsCopy = points.clone();
            Arrays.sort(pointsCopy, origin.slopeOrder());

            Point head = origin;
            Point tail = origin;
            double newSlope = 0;
            // Two points are "collinear" with each other
            int collinearPoints = 2;

            // Find each set of 4+ collinear points and save them as a line from the smallest to the largest
            for (int k = 0; k < j; k++) {
                double oldSlope = newSlope;

                Point destination = pointsCopy[k];
                newSlope = origin.slopeTo(destination);

                // If the current slope matches the previous slope, grow the line
                if (newSlope == oldSlope) {
                    collinearPoints++;

                    if (DEBUG) {
                        System.out.println(
                                origin + " is collinear with " + destination + ", making point #"
                                        + collinearPoints);
                    }

                    // If this point is smaller than the head, it's the new head
                    if (head.compareTo(destination) > 0) {
                        head = destination;
                    }
                    // If this point is larger than the tail, it's the new tail
                    if (tail.compareTo(destination) < 0) {
                        tail = destination;
                    }
                }
                // If the current slope does NOT match the previous slope, the line has ended
                // Record the line segment as least point to greatest point
                if (collinearPoints >= 4) {
                    // If this line is new, store it in a list of lines
                    boolean newLine = true;

                    if (DEBUG) System.out.println(
                            "Checking to see if " + (new LineSegment(head, tail)) + " is new");

                    // Compare this head and tail with all existing heads and tails
                    for (int y = 0, z = b.size(); y < z; y++) {
                        Point recordedHead = bHeads.get(y);
                        Point recordedTail = bTails.get(y);

                        // If slope is same and either the head or tail are the same, merge lines
                        if (head.slopeTo(recordedTail) == head.slopeTo(tail)) {
                            // If this segment has already been recorded, it's not a new line, break
                            if (recordedHead.equals(head) && recordedTail.equals(tail)) {
                                newLine = false;

                                if (DEBUG) System.out
                                        .println("...that line segment has been recorded 👎🏻");
                                break;
                            }
                            // Otherwise,
                            else {
                                // If the head or tail have already been recorded, it's not _new_
                                // But it still could possibly be merged with an existing segment
                                if (recordedHead.equals(head) || recordedTail.equals(tail)
                                        || recordedHead.equals(tail) || recordedTail.equals(head)) {
                                    if (DEBUG) System.out
                                            .println("...that line segment has been recorded 🤝");
                                    newLine = false;

                                    // If the head is smaller than the current head or tail is bigger than current tail, take the better
                                    if (recordedHead.compareTo(head) < 0) {
                                        bHeads.set(y, head);
                                    }
                                    else {
                                        if (DEBUG) System.out.println("Head isn't smaller");
                                    }
                                    if (recordedTail.compareTo(tail) > 0) {
                                        bHeads.set(y, tail);
                                    }
                                    else {
                                        if (DEBUG) System.out.println("Tail isn't bigger");
                                    }

                                    // If the head is the same as the recorded tail, take that other line's head
                                    if (recordedTail.equals(head)) {
                                        bHeads.set(y, recordedHead);
                                    }

                                    // If the tail is the same as the recorded head, take that other line's tail
                                    if (recordedHead.equals(tail)) {
                                        bTails.set(y, recordedTail);
                                    }
                                }
                            }
                        }
                    }

                    if (newLine) {
                        if (DEBUG) {
                            System.out.println("...that line segment has NOT been recorded 👍🏻");
                        }
                        n++;
                        bHeads.add(head);
                        bTails.add(tail);
                        b.add(new LineSegment(head, tail));
                    }

                    collinearPoints = 2;
                }
            }
            a = new LineSegment[n];
        }

        for (int i = 0; i < b.size(); i++) {
            a[i] = b.get(i);
        }
        // TODO Dedupe segments
        /*
        (14000, 10000) -> (32000, 10000)
        (14000, 10000) -> (32000, 10000)
        (14000, 10000) -> (32000, 10000)
        (14000, 10000) -> (32000, 10000)
        (14000, 10000) -> (32000, 10000)

        should return

        (14000, 10000) -> (32000, 10000)

        First, get them all going from small to big
        Second, remove duplicates

         */
    }

    // the number of line segments
    public int numberOfSegments() {
        return n;
    }

    // the line segments
    public LineSegment[] segments() {
        return a;
    }

    public static void main(String[] args) {
        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        System.out.println("=====");
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
