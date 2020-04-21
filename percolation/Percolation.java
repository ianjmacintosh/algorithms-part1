/* *****************************************************************************
 *  Name: Ian MacIntosh
 *  Date: 4/20/20
 *  Description: Make and edit percolation simulations for Princeton Algorithms course
 **************************************************************************** */

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private boolean[][] grid;
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
        grid = new boolean[gridSize][gridSize];
        lastUnion = gridSize * gridSize + 1;

        // Unify top row with 0
        for (int i = 1; i < gridSize + 1; i++) {
            unionMappings.union(0, i);
        }

        // Unify bottom row with lastUnion
        for (int i = xyTo1D(gridSize, 1) + 1; // i = Union representing first column of last row
             i <= xyTo1D(gridSize, gridSize) + 1; // When we haven't reached the end of the grid...
             i++) {
            unionMappings.union(lastUnion, i);
        }

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                grid[row][col] = false;
            }
        }
    }

    private void showInventory() {
        for (int row = 0; row < gridSize; row++) {
            System.out.print("Row " + row + "\n");
            for (int col = 0; col < gridSize; col++) {
                System.out.println(row + ", " + col + ": " + grid[row][col]);
            }
        }
    }

    private int xyTo1D(int row, int col) {
        return ((row - 1) * gridSize) + (col - 1);
    }

    private void checkBoundaries(int row, int col) {
        if (row <= 0 || col <= 0 || row > gridSize || col > gridSize) {
            throw new IllegalArgumentException("Out of bounds!");
        }
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        checkBoundaries(row, col);

        if (isOpen(row, col)) {
            return;
        }

        int unionValue = xyTo1D(row, col) + 1;
        grid[row - 1][col - 1] = true;

        openSites++;

        // Review adjacent cells to see if they are open
        if (row < gridSize && isOpen(row + 1, col)) { // Below
            unionMappings.union((xyTo1D(row + 1, col) + 1), unionValue);
        }
        if (row > 1 && isOpen(row - 1, col)) { // Above
            unionMappings.union((xyTo1D(row - 1, col) + 1), unionValue);
        }
        if (col < gridSize && isOpen(row, col + 1)) { // Right
            unionMappings.union((xyTo1D(row, col + 1) + 1), unionValue);
        }
        if (col > 1 && isOpen(row, col - 1)) { // Left
            unionMappings.union((xyTo1D(row, col - 1) + 1), unionValue);
        }
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        checkBoundaries(row, col);

        int gridRow = row - 1;
        int gridCol = col - 1;

        if (gridRow < 0 || gridCol < 0 || gridRow >= gridSize || gridCol >= gridSize) {
            return false;
        }
        return grid[row - 1][col - 1];
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        checkBoundaries(row, col);
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
        System.out.println("Open cells: " + test.numberOfOpenSites());
        System.out.println("Percolates: " + test.percolates());
        // test.showInventory();
    }
}
