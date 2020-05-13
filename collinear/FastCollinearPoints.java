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
    private LinkedList<Point> bHeads = new LinkedList<>();
    private LinkedList<Point> bTails = new LinkedList<>();

    // finds all line segments containing 4 or more points
    public FastCollinearPoints(Point[] points) {
        // Throw an IllegalArgumentException if:
        // * the argument to the constructor is null
        if (points == null) throw new IllegalArgumentException();

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

            //  Sort points by slope to origin
            Point[] sortedPoints = points.clone();
            Arrays.sort(sortedPoints, origin.slopeOrder());

            //  Store all points with same slope in an array of collinear points
            LinkedList<Point> collinearPoints = new LinkedList<>();
            collinearPoints.push(origin);
            // System.out.println("Found collinear point #" + collinearPoints.size() + ": "
            // +origin);
            double lastSlope = 0;
            double thisSlope = 0;
            Point lastPoint = points[i];
            Point thisPoint = points[i];

            for (int k = 0; k < j; k++) {
                lastPoint = thisPoint;
                thisPoint = sortedPoints[k];
                lastSlope = thisSlope;
                thisSlope = origin.slopeTo(thisPoint);
                // System.out.println(points[i] + " -> " + sortedPoints[k] + ": " + thisSlope);

                // If last slope and this slope are the same, add it to the collinear points
                if (thisSlope == lastSlope) {
                    collinearPoints.push(lastPoint);
                    // System.out.println("Found collinear point #" + collinearPoints.size() + ": "
                    // +lastPoint);
                }
                // If it doesn't match the current slope or we're at the end of points,
                // we've hit the end of this series of collinear points
                if ((collinearPoints.size() >= 3) && (thisSlope != lastSlope
                        || k == sortedPoints.length - 1)) {
                    collinearPoints.push(lastPoint);
                    // System.out.println("Found collinear point #" + collinearPoints.size() + ": "
                    // +lastPoint);
                    // System.out.println("Found " + collinearPoints.size() + " collinear points");
                    // Add the origin to this series of collinear points
                    //  If there are 4 or more points in that array:
                    Point[] seriesOfPoints = new Point[collinearPoints.size()];

                    for (int m = 0; m < collinearPoints.size(); m++) {
                        seriesOfPoints[m] = collinearPoints.get(m);
                        System.out.print(seriesOfPoints[m]);
                        if (m < collinearPoints.size() - 1) System.out.print(" -> ");
                        else System.out.print("\n");
                    }

                    Arrays.sort(seriesOfPoints, Point::compareTo);
                    Point min = seriesOfPoints[0];
                    Point max = seriesOfPoints[seriesOfPoints.length - 1];

                    // TODO Pass all the points instead of doing this min max thing
                    handleNewSegment(min, max, seriesOfPoints.length);
                    collinearPoints.clear();
                }
            }
        }

        a = new LineSegment[n];

        for (int i = 0; i < bHeads.size(); i++) {
            a[i] = new LineSegment(bHeads.get(i), bTails.get(i));
        }
    }

    // Determine if a point is new, and if so record it
    private void handleNewSegment(Point head, Point tail, int points) {
        System.out.println("Handling new " + points + "-point segment " + head + " -> " + tail);
        // If this line is new, store it in a list of lines
        boolean newLine = true;

        // Compare this head and tail with all existing heads and tails
        for (int y = 0; y < bHeads.size(); y++) {
            Point recordedHead = bHeads.get(y);
            Point recordedTail = bTails.get(y);

            // If slope is same and either the head or tail are the same
            if (recordedHead.slopeTo(recordedTail) == head.slopeTo(tail)) {
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
            System.out.println("Adding " + head + " -> " + tail);
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
