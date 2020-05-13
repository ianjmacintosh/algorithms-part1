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
            //  Store all points with same slope in an array of collinear points
            LinkedList<Point> collinearPoints = new LinkedList<>();
            Point origin = points[i];
            collinearPoints.push(origin);

            //  Sort points by slope to origin
            Point[] sortedPoints = points.clone();
            Arrays.sort(sortedPoints, origin.slopeOrder());

            // Repeat until no more points are left:
            for (int k = 0; k < sortedPoints.length; k++) {
                // Look at the next point
                Point thisPoint = sortedPoints[k];

                // Add next point to list of collinear points
                collinearPoints.push(thisPoint);

                // If this is the first collinear point, go back to start of this loop
                if (collinearPoints.size() == 2) continue;

                // If the slope from origin -> first collinear point is the same as origin -> last collinear point, pass it to the handler
                if (origin.slopeTo(collinearPoints.get(1)) == (origin.slopeTo(thisPoint))) {
                    if (collinearPoints.size() >= 4) {
                        handleNewSegment(collinearPoints);
                    }
                }
                // Otherwise, clear the collinear points, add this point again, go back to start of loop
                else {
                    collinearPoints.clear();
                    collinearPoints.push(origin);
                    collinearPoints.push(thisPoint);
                }

            }

        }
        a = new LineSegment[n];

        for (int i = 0; i < bHeads.size(); i++) {
            a[i] = new LineSegment(bHeads.get(i), bTails.get(i));
        }
    }

    // Determine if a point is new, and if so record it
    private void handleNewSegment(LinkedList<Point> points) {
        Point[] seriesOfPoints = new Point[points.size()];

        for (int m = 0; m < points.size(); m++) {
            seriesOfPoints[m] = points.get(m);
        }

        Arrays.sort(seriesOfPoints, Point::compareTo);

        // for (int m = 0; m < seriesOfPoints.length; m++) {
        //     System.out.print(seriesOfPoints[m]);
        //     if (m < points.size() - 1) System.out.print(" -> ");
        //     else System.out.print("\n");
        // }

        Point min = seriesOfPoints[0];
        Point max = seriesOfPoints[seriesOfPoints.length - 1];

        // Compare this segment to all segments
        for (int i = 0; i < n; i++) {
            // If the slope is the same
            if (bHeads.get(i).slopeTo(bTails.get(i)) == min.slopeTo(max)) {
                if (bHeads.get(i).equals(min) && bTails.get(i).equals(max)) {
                    return;
                }
            }
        }
        n++;
        bHeads.add(min);
        bTails.add(max);
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
