/* *****************************************************************************
 *  Name: Board representation
 *  Date: May 6, 2020
 *  Description: Store a board and provide utility classes to query and modify
 *      the board.
 **************************************************************************** */

import edu.princeton.cs.algs4.Stack;

import java.util.Arrays;

public class Board {
    private int[][] b;  // Array representing the board as a 2D array
    private int[] t;    // Array representing all tiles as a 1D array
    private int[] l;    // Array of tiles, each value as its location
    private int n;      // Board size (i.e., "3" if board is 3x3)
    private boolean p;  // Can the board possibly be solved?
    private boolean f = true;  // Is the board finished? (Optimistic)

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        if (tiles == null) throw new IllegalArgumentException();
        // If given a rectangular array, throw an error
        if (tiles.length != tiles[0].length) throw new IllegalArgumentException();

        // Cache the board's size (width/height)
        n = tiles.length;

        // Store the board as a 1D array, and keep a lookup table next to it
        t = new int[n * n];
        l = new int[n * n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {
                if (tiles[i][j] > n * n - 1) throw new IllegalArgumentException("Bad board");
                t[i * n + j] = tiles[i][j]; // t represents the board
                l[tiles[i][j]] = i * n + j; // l tells the location for each tile

                // If any tile is out of place (except 0), it is not solved
                if (tiles[i][j] != 0 &&
                        tiles[i][j] != i * n + j + 1) {
                    f = false;
                }
            }
    }

    // string representation of this board
    public String toString() {
        StringBuilder output = new StringBuilder();
        // Make a string of each row of the board as a string
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                if (j < n - 1) output.append(" " + t[n * i + j]);
                else output.append(" " + t[n * i + j] + "\n");

        // Return the board size and the board itself
        return n + "\n" + output.toString();
    }

    // board dimension n
    public int dimension() {
        // Should this be 2 (it's 2D)? Or the size of the board?
        return n;
    }

    // number of tiles out of place
    public int hamming() {
        int distance = 0;
        // Find out how many tiles are out of place
        // Check each space one by one
        //      If it's the wrong tile, increment the distance
        //      Ignore the last tile
        for (int i = 0, j = t.length; i < j - 1; i++)
            if (t[i] != i + 1) distance++;

        // If it's just 2: determine if they're in each other's place
        // If swapping them would fix things, the board is unsolvable!
        return distance;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        int distance = 0;

        // Review each space, one by one, and identify how many spaces away
        //  its correct tile is -- except the last square, ignore that since
        //  it's supposed to be empty
        for (int i = 0; i < t.length - 1; i++) {
            if (t[i] != i + 1) {
                // Add the distance from the current square and its correct tile
                distance += getDistance(i, l[i + 1]);
            }
        }
        return distance;
    }

    // Return the row a given square resides in
    private int getRow(int a) {
        return a / n;
    }

    // Return the column a given square resides in
    private int getCol(int a) {
        return a % n;
    }

    // Return the number of squares needed to travel from a to b
    private int getDistance(int a, int b) {
        // Add the difference in rows to the difference in cols
        //      To find the distance:
        //          Get the row of a    (ya)
        //          Get the row of b    (yb)
        //          Get the col of a    (xa)
        //          Get the col of b    (xb)
        //          abs(ya - yb) + abs(xa - xb) = d
        return Math.abs(getRow(a) - getRow(b)) + Math.abs(getCol(a) - getCol(b));
    }

    // is this board the goal board?
    public boolean isGoal() {
        // Is the board finished?
        return f;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        // Compare two boards and determine if they're the same
        // If the compared object is a reference to this object, yes
        if (y == this) return true;

        // If the compared object is null, no
        if (y == null) return false;

        // If they're not even the same class, no
        if (y.getClass() != this.getClass()) return false;

        // If they're different sizes, no
        Board that = (Board) y;
        if (this.dimension() != that.dimension()) return false;

        // If their tiles aren't identical, no
        for (int i = 0, j = this.dimension() * this.dimension(); i < j; i++) {
            if (this.t[i] != that.t[i]) return false;
        }

        // If the logic got this far, yes
        return true;
    }

    // Make a 2D array from a 1D array
    private int[][] make2DArrayFrom1DArray(int n, int[] t) {
        int[][] newArray = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                newArray[i][j] = t[n * i + j];
            }
        }
        return newArray;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        // List all possible boards that can be made by moving 1 tile
        // Could be 2, 3, or 4 long
        // Presume we have 4 possible options
        Stack<Board> boards = new Stack<Board>();

        // First, find what row & column the empty space ("0 tile") is in
        int x = getCol(l[0]);
        int y = getRow(l[0]);
        int tmp;

        int[] boardCopy;

        // If it's NOT in the first row
        // Swap the 0 tile and its up tile, add it to a list of next boards
        if (y != 0) {
            // TODO Dry these up
            tmp = t[l[0] - n]; // Store up tile in memory
            boardCopy = t.clone(); // Clone the tiles array
            boardCopy[l[0]] = tmp; // Put the up tile on the square location of 0
            boardCopy[l[0] - n] = 0; // Put 0 in the up square location
            boards.push(new Board(make2DArrayFrom1DArray(n, boardCopy)));
        }

        // If it's NOT in the last row
        // Swap the 0 tile and its below tile, add it to a list of next boards
        if (y != n - 1) {
            // TODO Dry these up
            tmp = t[l[0] + n]; // Store down tile in memory
            boardCopy = t.clone(); // Clone the tiles array
            boardCopy[l[0]] = tmp; // Put the up tile on the square location of 0
            boardCopy[l[0] + n] = 0; // Put 0 in the down square location
            boards.push(new Board(make2DArrayFrom1DArray(n, boardCopy)));
        }

        // If it's NOT in the last column
        // Swap the 0 tile and its right tile, add it to a list of next boards
        if (x != n - 1) {
            // TODO Dry these up
            tmp = t[l[0] + 1]; // Store right tile in memory
            boardCopy = t.clone(); // Clone the tiles array
            boardCopy[l[0]] = tmp; // Put the up tile on the square location of 0
            boardCopy[l[0] + 1] = 0; // Put 0 in the right square location
            boards.push(new Board(make2DArrayFrom1DArray(n, boardCopy)));
        }

        // If it's NOT in the first column
        // Swap the 0 tile its left tile, add it to a list of next boards
        if (x != 0) {
            // TODO Dry these up
            tmp = t[l[0] - 1]; // Store left down in memory
            boardCopy = t.clone(); // Clone the tiles array
            boardCopy[l[0]] = tmp; // Put the up tile on the square location of 0
            boardCopy[l[0] - 1] = 0; // Put 0 in the left square location
            boards.push(new Board(make2DArrayFrom1DArray(n, boardCopy)));
        }

        return boards;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        // Swap first 2 out-of-place tiles (tile a & tile b)
        int[] newTiles = t.clone();
        int a = -1; // Tile A
        int z = -1; // Tile B
        // Skim over squares, searching for an out-of-place tile
        for (int i = 0; i < n * n; i++)
            if (t[i] != 0 && t[i] != i + 1)
                if (a < 0) {
                    a = t[i];
                }
                else {
                    z = t[i];
                    break;
                }

        // If we couldn't find a 2nd out-of-place square
        if (a < 0) a = 1;
        if (z < 0)
            if (a != 1) z = 1;
            else z = 2;

        newTiles[l[a]] = t[l[z]];
        newTiles[l[z]] = t[l[a]];

        return new Board(make2DArrayFrom1DArray(n, newTiles));
    }

    // unit testing (not graded)


    // =======================================================================
    // LET THE WILD RUMPUS BEGIN!!!
    // =======================================================================

    private static void reportTests(String ts, int p, int e) {
        if (p == e) {
            System.out.println("✅ " + ts + " passed " + p + "/" + e + " tests");
        }
        else {
            System.out.println("🛑 " + ts + " passed " + p + "/" + e + " tests");
            if (p > e) System.out
                    .println("     Did you add a new test and forget to increment expected tests?");
        }
    }

    public static void main(String[] args) {
        String testSubject;
        String testDescription;
        int testsInSuite;
        int testsPassed;
        Board testBoard;
        Board testBoard2;
        int testCounter;

        // Make a test board
        int[][] solved2Board = {
                { 1, 2 },
                { 3, 0 }
        };

        int[][] solved3Board = {
                { 1, 2, 3 },
                { 4, 5, 6 },
                { 7, 8, 0 }
        };

        int[][] impossible3Board = {
                { 1, 2, 3 },
                { 4, 5, 6 },
                { 8, 7, 0 }
        };

        int[][] exampleUnsolved3Board = {
                { 8, 1, 3 },
                { 4, 0, 2 },
                { 7, 6, 5 }
        };

        int[][] solved4Board = {
                { 1, 2, 3, 4 },
                { 5, 6, 7, 8 },
                { 9, 10, 11, 12 },
                { 13, 14, 15, 0 }
        };

        int[][] rectangularBoardLayout = {
                { 1, 2 },
                { 3, 4 },
                { 4, 0 }
        };

        int[][] LShapedBoardLayout = {
                { 1, 2 },
                { 3, 4 },
                { 5, 6, 0 }
        };

        int[][] rootUnsolved3Board = {
                { 0, 1, 3 },
                { 4, 2, 5 },
                { 7, 8, 6 }
        };

        /*
        The Board constructor should tell good boards from bad
         */
        testSubject = "Board constructor";
        testsPassed = 0;

        testDescription = "Board constructor throws no error if provided a square array";
        try {
            testBoard = new Board(solved3Board);
            testsPassed++;
        }
        catch (IllegalArgumentException exception) {
            System.out.println("FAIL: " + testDescription);
        }

        testDescription = "Board constructor throws an error if provided a rectangular array";
        try {
            testBoard = new Board(rectangularBoardLayout);
            System.out.println("FAIL: " + testDescription);
        }
        catch (IllegalArgumentException exception) {
            testsPassed++;
        }

        testDescription = "Board constructor throws an error if provided an L-shaped array";
        try {
            testBoard = new Board(LShapedBoardLayout);
            System.out.println("FAIL: " + testDescription);
        }
        catch (IllegalArgumentException exception) {
            testsPassed++;
        }

        reportTests(testSubject, testsPassed, 3);

        /*
        A Board holds an n-by-n array. Board.dimension() returns n
         */
        testSubject = "Board.dimension()";
        testsPassed = 0;

        testDescription = "Board.dimension returns 3 when handling a 3x3 board";
        testBoard = new Board(solved3Board);
        if (testBoard.dimension() == 3) {
            testsPassed++;
        }
        else {
            System.out.println("Returned " + testBoard.dimension());
            System.out.println("FAIL: " + testDescription);
        }

        testDescription = "Board.dimension returns 4 when handling a 4x4 board";
        testBoard = new Board(solved4Board);
        if (testBoard.dimension() == 4) {
            testsPassed++;
        }
        else {
            System.out.println("Returned " + testBoard.dimension());
            System.out.println("FAIL: " + testDescription);
        }

        reportTests(testSubject, testsPassed, 2);

        /*
        Board.toString() returns string representation of this board
         */
        testSubject = "Board.toString()";
        testsPassed = 0;

        testDescription = "Board.toString() returns a string of a 3x3 board";
        testBoard = new Board(solved3Board);
        String solved3BoardString = "3\n 1 2 3\n 4 5 6\n 7 8 0\n";

        if (testBoard.toString().equals(solved3BoardString)) {
            testsPassed++;
        }
        else {
            System.out.println("Returned " + testBoard.toString());
            System.out.println("FAIL: " + testDescription);
        }

        reportTests(testSubject, testsPassed, 1);

        /*
        Board.hamming() returns the board's Hamming Distance
         */
        testSubject = "Board.hamming()";
        testsPassed = 0;

        testDescription = "Board.hamming() returns 0 for a solved board";
        testBoard = new Board(solved3Board);

        if (testBoard.hamming() == 0) {
            testsPassed++;
        }
        else {
            System.out.println("Returned " + testBoard.hamming());
            System.out.println("FAIL: " + testDescription);
        }

        testDescription = "Board.hamming() returns 5 for the spec example board";
        testBoard = new Board(exampleUnsolved3Board);

        if (testBoard.hamming() == 5) {
            testsPassed++;
        }
        else {
            System.out.println("Returned " + testBoard.hamming());
            System.out.println("FAIL: " + testDescription);
        }

        reportTests(testSubject, testsPassed, 2);

        /*
        Board.getRow() returns the row of a square
         */
        testSubject = "Board.getRow()";
        testsPassed = 0;

        testDescription = "Board.getRow(0) shows that square 0 is on row 0";
        testBoard = new Board(solved3Board);

        if (testBoard.getRow(0) == 0) {
            testsPassed++;
        }
        else {
            System.out.println("Returned " + testBoard.getRow(0));
            System.out.println("FAIL: " + testDescription);
        }

        testDescription = "Board.getRow() shows that square 3 is on row 1";
        testBoard = new Board(solved3Board);

        if (testBoard.getRow(3) == 1) {
            testsPassed++;
        }
        else {
            System.out.println("Returned " + testBoard.getRow(3));
            System.out.println("FAIL: " + testDescription);
        }

        reportTests(testSubject, testsPassed, 2);

        /*
        Board.getCol() returns the row of a square
         */
        testSubject = "Board.getCol()";
        testsPassed = 0;

        testDescription = "Board.getCol(0) shows that square 0 is on col 0";
        testBoard = new Board(solved3Board);

        if (testBoard.getCol(0) == 0) {
            testsPassed++;
        }
        else {
            System.out.println("Returned " + testBoard.getCol(0));
            System.out.println("FAIL: " + testDescription);
        }

        testDescription = "Board.getRow() shows that square 5 is on row 2";
        testBoard = new Board(solved3Board);

        if (testBoard.getCol(5) == 2) {
            testsPassed++;
        }
        else {
            System.out.println("Returned " + testBoard.getRow(5));
            System.out.println("FAIL: " + testDescription);
        }

        reportTests(testSubject, testsPassed, 2);

        /*
        Board.getCol() returns the row of a square
         */
        testSubject = "Board.getDistance()";
        testsPassed = 0;

        testDescription = "Board.getDistance(0, 5) shows 0 and 5 are 3 squares apart";
        testBoard = new Board(solved3Board);

        if (testBoard.getDistance(0, 5) == 3) {
            testsPassed++;
        }
        else {
            System.out.println("Returned " + testBoard.getDistance(0, 5));
            System.out.println("FAIL: " + testDescription);
        }

        testDescription = "Board.getDistance(4, 5) shows 4 and 5 are 1 square apart";
        testBoard = new Board(solved3Board);

        if (testBoard.getDistance(4, 5) == 1) {
            testsPassed++;
        }
        else {
            System.out.println("Returned " + testBoard.getDistance(4, 5));
            System.out.println("FAIL: " + testDescription);
        }

        testDescription = "Board.getDistance(2, 2) shows 2 and 2 are 0 squares apart";
        testBoard = new Board(solved3Board);

        if (testBoard.getDistance(2, 2) == 0) {
            testsPassed++;
        }
        else {
            System.out.println("Returned " + testBoard.getDistance(2, 2));
            System.out.println("FAIL: " + testDescription);
        }

        reportTests(testSubject, testsPassed, 3);

        /*
        Board.manhattan() returns the row of a square
         */
        testSubject = "Board.manhattan()";
        testsPassed = 0;

        testDescription = "Board.manhattan() gets 10 for the example board";
        testBoard = new Board(exampleUnsolved3Board);

        if (testBoard.manhattan() == 10) {
            testsPassed++;
        }
        else {
            System.out.println("Returned " + testBoard.manhattan());
            System.out.println("FAIL: " + testDescription);
        }

        testDescription = "Board.manhattan() gets 4 for the example root board";
        testBoard = new Board(rootUnsolved3Board);

        if (testBoard.manhattan() == 4) {
            testsPassed++;
        }
        else {
            System.out.println("Returned " + testBoard.manhattan());
            System.out.println("FAIL: " + testDescription);
        }

        reportTests(testSubject, testsPassed, 2);

        /*
        Board.isGoal() returns if a board is solved
         */
        testSubject = "Board.isGoal()";
        testsPassed = 0;

        testDescription = "Board.isGoal() returns true if given a solved board";
        testBoard = new Board(solved3Board);

        if (testBoard.isGoal()) {
            testsPassed++;
        }
        else {
            System.out.println("Returned " + testBoard.isGoal() + " for " + testBoard.toString());
            System.out.println("FAIL: " + testDescription);
        }

        testDescription = "Board.isGoal() returns false if given an unsolved board";
        testBoard = new Board(exampleUnsolved3Board);

        if (!testBoard.isGoal()) {
            testsPassed++;
        }
        else {
            System.out.println("Returned " + testBoard.isGoal() + " for " + testBoard.toString());
            System.out.println("FAIL: " + testDescription);
        }

        reportTests(testSubject, testsPassed, 2);

        /*
        Board.equals() compares boards and says if they're identical
         */
        testSubject = "Board.equals()";
        testsPassed = 0;

        testDescription = "Board.equals() returns false if given a different board";
        testBoard = new Board(exampleUnsolved3Board);
        testBoard2 = new Board(rootUnsolved3Board);

        if (!testBoard.equals(testBoard2)) {
            testsPassed++;
        }
        else {
            System.out.println("Returned " + testBoard.equals(testBoard2));
            System.out.println("FAIL: " + testDescription);
        }

        testDescription = "Board.equals() returns true if given the identical board";
        testBoard = new Board(new int[][] { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 0 } });
        testBoard2 = new Board(solved3Board);

        if (testBoard.equals(testBoard2)) {
            testsPassed++;
        }
        else {
            System.out.println("Returned " + testBoard.equals(testBoard2));
            System.out.println("FAIL: " + testDescription);
        }

        testDescription
                = "Board.equals() returns false if given the two boards it failed on before";
        Board testBoardA = new Board(new int[][] { { 1, 2, 3 }, { 4, 8, 5 }, { 7, 6, 0 } });
        Board testBoardB = new Board(new int[][] { { 1, 2, 3 }, { 4, 0, 5 }, { 7, 8, 6 } });

        if (!testBoardA.equals(testBoardB)) {
            testsPassed++;
        }
        else {
            System.out.println("Returned " + testBoardA.equals(testBoardB));
            System.out.println("FAIL: " + testDescription);
        }

        reportTests(testSubject, testsPassed, 3);

        /*
        Board.make2DArrayFrom1DArray() makes a 2D array from a 1D array
         */
        testSubject = "Board.make2DArrayFrom1DArray()";
        testsPassed = 0;

        testDescription
                = "Board.make2DArrayFrom1DArray() makes { { 1, 2 }, { 3, 4 } } from { 1, 2, 3, 4 }";
        testBoard = new Board(solved2Board);

        if (Arrays.deepEquals(testBoard.make2DArrayFrom1DArray(2, new int[] { 1, 2, 3, 0 }),
                              solved2Board)) {
            testsPassed++;
        }
        else {
            System.out.println("Returned " + Arrays.deepEquals(solved3Board, solved3Board));
            System.out.println("FAIL: " + testDescription);
        }

        reportTests(testSubject, testsPassed, 1);

        /*
        Board.neighbors() returns other possible boards
         */
        testSubject = "Board.neighbors()";
        testsInSuite = 0;
        testsPassed = 0;

        testDescription = "Board.neighbors() gives 4 options for a unsolved 3 board";
        testsInSuite++;
        testBoard = new Board(exampleUnsolved3Board);

        testCounter = 0;
        for (Board dummyBoard : testBoard.neighbors()) {
            testCounter++;
        }
        if (testCounter == 4) {
            testsPassed++;
        }
        else {
            System.out.println("Returned " + testCounter + " options");
            System.out.println("FAIL: " + testDescription);
        }

        testDescription = "Board.neighbors() gives 2 options when top left is empty";
        testsInSuite++;
        testBoard = new Board(new int[][] { { 0, 1 }, { 2, 3 } });

        testCounter = 0;
        for (Board dummyBoard : testBoard.neighbors()) {
            testCounter++;
        }
        if (testCounter == 2) {
            testsPassed++;
        }
        else {
            System.out.println("Returned " + testCounter + " options");
            System.out.println("FAIL: " + testDescription);
        }

        testDescription = "Board.neighbors() gives 2 options when top right is empty";
        testsInSuite++;
        testBoard = new Board(new int[][] { { 1, 0 }, { 2, 3 } });

        testCounter = 0;
        for (Board dummyBoard : testBoard.neighbors()) {
            testCounter++;
        }
        if (testCounter == 2) {
            testsPassed++;
        }
        else {
            System.out.println("Returned " + testCounter + " options");
            System.out.println("FAIL: " + testDescription);
        }

        testDescription = "Board.neighbors() gives 2 options when bottom left is empty";
        testsInSuite++;
        testBoard = new Board(new int[][] { { 1, 2 }, { 0, 3 } });

        testCounter = 0;
        for (Board dummyBoard : testBoard.neighbors()) {
            testCounter++;
        }
        if (testCounter == 2) {
            testsPassed++;
        }
        else {
            System.out.println("Returned " + testCounter + " options");
            System.out.println("FAIL: " + testDescription);
        }

        testDescription = "Board.neighbors() gives 2 options when bottom right is empty";
        testsInSuite++;
        testBoard = new Board(solved3Board);

        testCounter = 0;
        for (Board dummyBoard : testBoard.neighbors()) {
            testCounter++;
        }
        if (testCounter == 2) {
            testsPassed++;
        }
        else {
            System.out.println("Returned " + testCounter + " options");
            System.out.println("FAIL: " + testDescription);
        }

        testDescription = "Board.neighbors() gives 3 options when top middle is empty";
        testsInSuite++;
        testBoard = new Board(new int[][] { { 1, 0, 2 }, { 3, 4, 5 }, { 6, 7, 8 } });

        testCounter = 0;
        for (Board dummyBoard : testBoard.neighbors()) {
            testCounter++;
        }
        if (testCounter == 3) {
            testsPassed++;
        }
        else {
            System.out.println("Returned " + testCounter + " options");
            System.out.println("FAIL: " + testDescription);
        }

        testDescription = "Board.neighbors() gives 3 options when middle left is empty";
        testsInSuite++;
        testBoard = new Board(new int[][] { { 1, 3, 2 }, { 0, 4, 5 }, { 6, 7, 8 } });

        testCounter = 0;
        for (Board dummyBoard : testBoard.neighbors()) {
            testCounter++;
        }
        if (testCounter == 3) {
            testsPassed++;
        }
        else {
            System.out.println("Returned " + testCounter + " options");
            System.out.println("FAIL: " + testDescription);
        }

        testDescription = "Board.neighbors() gives 3 options when middle right is empty";
        testsInSuite++;
        testBoard = new Board(new int[][] { { 1, 3, 2 }, { 5, 4, 0 }, { 6, 7, 8 } });

        testCounter = 0;
        for (Board dummyBoard : testBoard.neighbors()) {
            testCounter++;
        }
        if (testCounter == 3) {
            testsPassed++;
        }
        else {
            System.out.println("Returned " + testCounter + " options");
            System.out.println("FAIL: " + testDescription);
        }

        testDescription = "Board.neighbors() gives 3 options when bottom middle is empty";
        testsInSuite++;
        testBoard = new Board(new int[][] { { 1, 3, 2 }, { 5, 4, 7 }, { 6, 0, 8 } });

        testCounter = 0;
        for (Board dummyBoard : testBoard.neighbors()) {
            testCounter++;
        }
        if (testCounter == 3) {
            testsPassed++;
        }
        else {
            System.out.println("Returned " + testCounter + " options");
            System.out.println("FAIL: " + testDescription);
        }

        reportTests(testSubject, testsPassed, testsInSuite);

        /*
        Board.twin() swaps the 1 and 2 tiles
         */
        testSubject = "Board.twin()";
        testsInSuite = 0;
        testsPassed = 0;

        testDescription = "Board.twin() swaps the 1 & 2 tiles on a solved 3 board";
        testsInSuite++;
        testBoard = new Board(solved3Board);

        testCounter = 0;
        if (testBoard.twin()
                     .equals(new Board(new int[][] { { 2, 1, 3 }, { 4, 5, 6 }, { 7, 8, 0 } }))) {
            testsPassed++;
        }
        else {
            System.out.println("Returned " + testCounter + " options");
            System.out.println("FAIL: " + testDescription);
        }

        testDescription = "Board.twin() swaps the 1 & 2 tiles on a solved 2 board";
        testsInSuite++;
        testBoard = new Board(solved2Board);

        testCounter = 0;
        if (testBoard.twin()
                     .equals(new Board(new int[][] { { 2, 1 }, { 3, 0 } }))) {
            testsPassed++;
        }
        else {
            System.out.println("Returned " + testCounter + " options");
            System.out.println("FAIL: " + testDescription);
        }

        testDescription = "Board.twin() swaps the 7 & 8 tiles on an impossible 3 board";
        testsInSuite++;
        testBoard = new Board(impossible3Board);

        testCounter = 0;
        if (testBoard.twin()
                     .equals(new Board(solved3Board))) {
            testsPassed++;
        }
        else {
            System.out.println("Returned " + testCounter + " options");
            System.out.println("FAIL: " + testDescription);
        }

        reportTests(testSubject, testsPassed, testsInSuite);
    }
}
