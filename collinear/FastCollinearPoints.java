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
    private int n = 0; // Number of straight lines
    private LineSegment[] a; // Collection of straight lines

    // finds all line segments containing 4 or more points
    public FastCollinearPoints(Point[] points) {
        // Throw an IllegalArgumentException if:
        // * the argument to the constructor is null
        if (points == null) throw new IllegalArgumentException();

        LinkedList<Point> bHeads = new LinkedList<>();
        LinkedList<Point> bTails = new LinkedList<>();
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

                    // If this point is smaller than the head, it's the new head
                    if (head.compareTo(destination) > 0) {
                        head = destination;
                    }
                    // If this point is larger than the tail, it's the new tail
                    if (tail.compareTo(destination) < 0) {
                        tail = destination;
                    }
                }
                // Record the line segment as least point to greatest point
                if (collinearPoints >= 4) {
                    // If this line is new, store it in a list of lines
                    boolean newLine = true;

                    // Compare this head and tail with all existing heads and tails
                    for (int y = 0; y < bHeads.size(); y++) {
                        Point recordedHead = bHeads.get(y);
                        Point recordedTail = bTails.get(y);

                        // If slope is same and either the head or tail are the same
                        if (head.slopeTo(recordedTail) == head.slopeTo(tail)) {
                            // Make sure we don't make a new line
                            newLine = false;
                            // If this segment has already been recorded, it's not a new line, break
                            if (recordedHead.equals(head) && recordedTail.equals(tail)) break;
                            else {
                                // If the head is smaller than the current head or tail is bigger than current tail, take the better
                                if (recordedHead.compareTo(head) > 0) bHeads.set(y, head);
                                if (recordedTail.compareTo(tail) < 0) bTails.set(y, tail);
                            }
                        }
                    }

                    if (newLine) {
                        n++;
                        bHeads.add(head);
                        bTails.add(tail);
                    }

                    collinearPoints = 2;
                }
            }
            a = new LineSegment[n];
        }

        for (int i = 0; i < bHeads.size(); i++) {
            a[i] = new LineSegment(bHeads.get(i), bTails.get(i));
        }
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
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
