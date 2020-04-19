// Develop a plan of attack (steps) [this, in other words]
// Read spec all the way through
// Write tests from memory of spec
// Re-read spec and locate where each requirement is represented in test
// Write application to spec
// Add tests where appropriate

// Spec to write:

// All API methods described exist:
// * Percolation
//      Percolation(int n)
//      open(int row, int col)
//      isOpen(int row, int col)
//      isFull(int row, int col)
//      numberOfOpenSites()
//      percolates()
//      May optionally return values when called without a method?



// All UnionFind methods have been imported
//      union()
//      find()
//      connected()
//      count()

// Percolation constructor holds an array to represent grid
// Percolation constructor throws error when given arg < 0
// open() method changes Percolation array persistently:
// Calling isOpen() on 1,1 and 1,2 returns true for both
// Calling isFull() on 1,1 and 1,2 returns false for both
// Make a new Percolation(2) and call open(1,1) and (2,1)
//   and call isFull(1,1) and isFull(2,1) returns true for both
// Make a new Percolation(3) and call open(1,1), (2,1), (3,1),
//   and (3,3), confirm isFull(3,3) returns false and isFull(1,3)
//   returns true