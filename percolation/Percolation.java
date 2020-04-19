/* *****************************************************************************
 *  Name: Ian MacIntosh
 *  Date: idk
 *  Description: idk
 **************************************************************************** */

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private boolean[] grid;
    private WeightedQuickUnionUF unionMappings;
    private int gridSize;
    private int openSites;
    private int lastUnion;

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Grid must be larger than 0");
        }

        gridSize = n;
        openSites = 0;
        unionMappings = new WeightedQuickUnionUF(gridSize * gridSize + 2);
        grid = new boolean[gridSize * gridSize];
        lastUnion = gridSize * gridSize + 1;

        // Unify top row with 0
        for (int i = 1; i < gridSize + 1; i++) {
            unionMappings.union(0, i);
        }

        // Unify bottom row with lastUnion
        for (int lastCell = gridSize * gridSize, // lastCell = End of grid
             i = lastCell - gridSize; // i = End of grid (9) - size of grid (3)
             i < lastCell; // When we haven't reached the end of the grid...
             i++) {
            unionMappings.union(lastUnion, i);
        }
        for (int loc = 0; loc < grid.length; loc++) {
            grid[loc] = false;
        }
    }

    private int xyTo1D(int row, int col) {
        System.out.println(row + ", " + col + " is [" + ((row - 1) * gridSize + (col - 1)) + "]");
        return (row - 1) * gridSize + (col - 1);
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        if (isOpen(row, col)) {
            return;
        }

        grid[xyTo1D(row, col)] = true;

        openSites++;

        // Review adjacent cells to see if they are open
        System.out.println("Cell below...");
        if (isOpen(row + 1, col)) { // Below
            unionMappings.union(xyTo1D(row + 1, col) + 1, xyTo1D(row, col) + 1);
        }
        System.out.println("Cell above...");
        if (isOpen(row - 1, col)) { // Above
            unionMappings.union(xyTo1D(row - 1, col) + 1, xyTo1D(row, col) + 1);
        }
        System.out.println("Cell right...");
        if (isOpen(row, col + 1)) { // Right
            unionMappings.union(xyTo1D(row, col + 1) + 1, xyTo1D(row, col) + 1);
        }
        System.out.println("Cell left...");
        if (isOpen(row, col - 1)) { // Left
            unionMappings.union(xyTo1D(row, col - 1) + 1, xyTo1D(row, col) + 1);
        }
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        // Load the "grid" variable from Percolation in memory
        // Review the value stored at [row][col]
        // If that value represents "open" or "open and full", return true
        // Otherwise, return false
        if (row < 1 || col < 1 || row > gridSize || col > gridSize) {
            return false;
        }
        System.out.println("Checking if " + row + ", " + col + " is open");
        return grid[xyTo1D(row, col)];
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        // Otherwise, return false
        return unionMappings.connected(0, xyTo1D(row, col) + 1);
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return openSites;
    }

    // does the system percolate?
    public boolean percolates() {
        return unionMappings.connected(0, lastUnion);
    }

    // test client (optional)
    public static void main(String[] args) {
        Percolation test = new Percolation(3);
        test.open(1, 1);
        test.open(1, 2);
        test.open(2, 1);
        test.open(2, 2);
        test.open(3, 2);
        System.out.println("Open cells? Say no more fam: " + test.numberOfOpenSites()); // Expect 3
        System.out.println("Percolates: " + test.percolates());

    }
}
