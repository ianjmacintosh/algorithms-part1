/* *****************************************************************************
 *  Name: Ian MacIntosh
 *  Date: Apr 27, 2020
 *  Description: Exercise for Algorithms, Part I (Princeton University)
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.LinkedList;

public class BruteCollinearPoints {
    private int n; // Number of straight lines
    private LineSegment[] a; // Collection of straight lines

    // finds all line segments containing 4 points
    public BruteCollinearPoints(Point[] points) {
        if (points == null) {
            throw new IllegalArgumentException();
        }

        LinkedList<LineSegment> b = new LinkedList<>();
        n = 0;
        int ps = points.length;
        if (points.length == 0) throw new IllegalArgumentException();
        for (int i = 0; i < ps; i++) {
            Point thisPoint = points[i];
            if (thisPoint == null) throw new IllegalArgumentException();

            // Check if this point exists elsewhere in the array
            points[i] = null;
            for (int k = 0; k < ps; k++) {
                if (thisPoint.equals(points[k])) throw new IllegalArgumentException();
            }
            points[i] = thisPoint;
        }

        for (int lp = 0; lp < ps; lp++) {
            for (int lq = 0; lq < ps; lq++) {
                for (int lr = 0; lr < ps; lr++) {
                    for (int ls = 0; ls < ps; ls++) {
                        if (allEqualSlopes(points[lp], points[lq], points[lr], points[ls])) {
                            n++;
                            // Add it on to a linked list
                            b.add(new LineSegment(points[lp], points[ls]));
                        }
                    }
                }
            }
        }

        // Now we can set up `a` with length of n and iterate over the linked
        // list to populate the array.
        a = new LineSegment[n];

        for (int i = 0; i < b.size(); i++) {
            a[i] = b.get(i);
        }
    }

    private boolean allEqualSlopes(Point p, Point q, Point r, Point s) {
        // Eliminate duplicates
        if (p.equals(q) || p.equals(r) || p.equals(s) || q.equals(r) || q.equals(s) || r
                .equals(s)) {
            return false;
        }
        // All slopes are equal
        if (p.slopeTo(q) == p.slopeTo(r) && p.slopeTo(r) == p.slopeTo(s)) {
            // Return false when the points aren't in order
            if (p.compareTo(q) < 0 && q.compareTo(r) < 0 && r.compareTo(s) < 0) {
                return true;
            }
        }
        return false;
    }

    // the number of line segments
    public int numberOfSegments() {
        return n;
    }

    // the line segments
    public LineSegment[] segments() {
        /*
        The method segments() should include each line segment containing 4
        points exactly once. If 4 points appear on a line segment in the order
        p→q→r→s, then you should include either the line segment p→s or s→p
        (but not both) and you should not include subsegments such as p→r or
        q→r. For simplicity, we will not supply any input to
        BruteCollinearPoints that has 5 or more collinear points.
         */
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
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
