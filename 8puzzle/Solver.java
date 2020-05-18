/* *****************************************************************************
 *  Name: Solver for 8 Puzzle
 *  Date: May 6, 2020
 *  Description: Representation of a board's solution its solution's properties
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdOut;

import java.util.LinkedList;

public class Solver {
    private Board startingBoard;
    private int moves = 0; // How many moves it took to get to the goal
    private boolean solvable = true;
    private LinkedList<Board> stepsList = new LinkedList<Board>();
    private MinPQ<SearchNode> pq = new MinPQ<SearchNode>();

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) throw new IllegalArgumentException();
        // Save the provided board
        startingBoard = initial;

        /*
        We define a search node of the game to be a board, the number of moves
        made to reach the board, and the previous search node. First, insert
        the initial search node (the initial board, 0 moves, and a null
        previous search node) into a priority queue. Then, delete from the
        priority queue the search node with the minimum priority, and insert
        onto the priority queue all neighboring search nodes (those that can be
        reached in one move from the dequeued search node). Repeat this
        procedure until the search node dequeued corresponds to the goal board.
         */
        // Run through A* algorithm until arriving at solution:
        // Insert the initial board into a priority queue
        SearchNode rootNode = new SearchNode(startingBoard, moves, null);
        pq.insert(rootNode);
        SearchNode thisStep = null;

        // Find the goal board
        // Repeat until dequeued search node is the goal board
        while (true) {
            // Delete the search node with the minimum priority
            // TODO Only dequeue neighbors from last board!
            thisStep = pq.delMin();
            // System.out.println(
            //         "Dequeuing move #" + thisStep.moves + "\nPriority=" + thisStep.priority);
            // System.out.println(thisStep.board);

            // If you've reached the goal, build the list of steps
            if (thisStep.board.isGoal()) {
                moves = thisStep.moves;
                stepsList.push(thisStep.board);

                // Climb through previous boards until hitting the root
                while (!thisStep.equals(rootNode)) {
                    thisStep = thisStep.previousNode;
                    stepsList.push(thisStep.board);
                } // while
                break;
            } // if

            // While running algorithm, regularly check if board is unsolvable
            if (thisStep.board.hamming() == 2 && thisStep.board.twin().isGoal()) {
                moves = -1;
                solvable = false;
                stepsList = null;
                break;
            }

            // Insert onto the priority queue all neighboring search nodes
            for (Board neighborBoard : thisStep.board.neighbors()) {
                // System.out.println("Looking for possible move #" + moves);
                // TODO "thisStep" is not necessarily the correct past step;
                /*
                If a more appealing board lies on the stack, take it and recalculate the number of moves
                Chase previous steps back to rootNode to calculate number of moves
                 */

                SearchNode possibleStep = new SearchNode(neighborBoard, thisStep.moves + 1,
                                                         thisStep);
                /*
                The critical optimization. A* search has one annoying feature:
                search nodes corresponding to the same board are enqueued on
                the priority queue many times (e.g., the bottom-left search
                node in the game-tree diagram above). To reduce unnecessary
                exploration of useless search nodes, when considering the
                neighbors of a search node, don’t enqueue a neighbor if its
                board is the same as the board of the previous search node in
                the game tree.
                 */

                if (thisStep.previousNode == null) {
                    // System.out.println(
                    //         "Queuing board possible move #" + possibleStep.moves + "\nPriority="
                    //                 + possibleStep.priority);
                    // System.out.println(possibleStep.board);
                    pq.insert(possibleStep);
                }
                else if (!neighborBoard.equals(thisStep.previousNode.board)) {
                    // System.out.println(
                    //         "Queuing board possible move #" + possibleStep.moves + "\nPriority="
                    //                 + possibleStep.priority);
                    // System.out.println(possibleStep.board);
                    pq.insert(possibleStep);
                }
            }
        }
    }

    private class SearchNode implements Comparable<SearchNode> {
        private SearchNode previousNode;
        private int manhattan;
        private int moves;
        // Avoid using Hamming because it leads to bad solutions
        // private int hamming;
        private Board board;
        private int priority;

        private SearchNode(Board b, int m, SearchNode p) {
            board = b;
            previousNode = p;
            // Calculate moves by chasing previous nodes to rootNode
            moves = m;
            manhattan = b.manhattan();
            priority = manhattan + moves;
        }

        public int compareTo(SearchNode that) {
            return Integer.compare(this.priority, that.priority);
        }
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        // Board is solvable if more than 2 pieces are out of place
        // Board is unsolvable if 2 pieces can be swapped to solve

        return solvable;
    }

    // min number of moves to solve initial board
    public int moves() {
        // Get the number of moves to solve, which we found during init
        return moves;
    }

    // sequence of boards in a shortest solution
    public Iterable<Board> solution() {
        // Explain how to iterate through the steps to solving (defined at init)
        return stepsList;
    }

    // test client (see below)
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }

}
