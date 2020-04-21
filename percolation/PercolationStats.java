/* *****************************************************************************
 *  Name: Ian MacIntosh
 *  Date: 4/20/20
 *  Description: Shows percolation stats from Princeton Algorithms course
 **************************************************************************** */

import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    private double[] trialRecord;

    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {
        int randomRow;
        int randomCol;
        trialRecord = new double[trials];

        for (int i = 0; i < trials; i++) {
            Percolation test = new Percolation(n);
            while (!test.percolates()) {
                randomRow = StdRandom.uniform(1, n + 1);
                randomCol = StdRandom.uniform(1, n + 1);
                test.open(randomRow, randomCol);
            }

            trialRecord[i] = test.numberOfOpenSites() / ((double) n * n);
        }
    }

    // sample mean of percolation threshold
    public double mean() {
        return StdStats.mean(trialRecord);
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return StdStats.stddev(trialRecord);
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        double ci95 = 1.96 * stddev() / Math.sqrt(trialRecord.length);
        return mean() - ci95;
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        double ci95 = 1.96 * stddev() / Math.sqrt(trialRecord.length);
        return mean() + ci95;
    }

    // test client (see below)
    public static void main(String[] args) {
        int n;
        int trials;

        if (args.length < 2) {
            n = 10;
            trials = 30;
        }
        else {
            n = Integer.parseInt(args[0]);
            trials = Integer.parseInt(args[1]);
        }
        if (n <= 0 || trials <= 0) {
            throw new IllegalArgumentException("We want more nuance!!");
        }
        PercolationStats test = new PercolationStats(n, trials);
        System.out.println("mean                    = " + test.mean());
        System.out.println("stddev                  = " + test.stddev());
        System.out.println(
                "95% confidence interval = " + test.confidenceLo() + "," + test.confidenceHi());
    }

}
