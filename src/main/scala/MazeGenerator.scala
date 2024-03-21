import scala.collection.mutable
import scala.util.Random

object MazeGenerator {
  
  // uses recursive backtracking to create a maze
  def generateMaze(rows: Int, cols: Int): Array[Array[Boolean]] = {
    val grid = Array.ofDim[Boolean](rows, cols)
    val stack = mutable.Stack[Cell]() // stack to keep track of visitied cells
    val rand = new Random()

    val startCell = Cell(rows - 1, cols - 1) // start from bottom right (end of the maze)

    // marks cell as visited
    def markVisited(cell: Cell): Unit = {
      grid(cell.row)(cell.col) = true
    }

    // checks if all neighbors are visited
    def allVisited(cell: Cell): Boolean = {
      cell.neighbors(grid).forall(n =>
        grid(n.row)(n.col))
    }

    // recursive function to generate maze. Explained in detail in the document.
    def generate(givenCell: Cell): Unit = {
      var current = givenCell
      markVisited(current)
      stack.push(current)
      while (stack.nonEmpty) {
        val neighbors = current.neighbors(grid).filterNot(allVisited)
        if (neighbors.nonEmpty) {
          val randomNeighbor = neighbors(rand.nextInt(neighbors.length))
          val passage = Cell(
            (current.row + randomNeighbor.row) / 2,
            (current.col + randomNeighbor.col) / 2
          )
          if (passage.valid(grid)) {
            grid(current.row)(current.col) = true
            grid(passage.row)(passage.col) = true
            grid(randomNeighbor.row)(randomNeighbor.col) = true
            stack.push(randomNeighbor)
            current = randomNeighbor
          }
        } else {
          current = stack.pop()
        }
      }
    }

    generate(startCell) // function call to create the maze

    // clearing some cells in the middle of the maze to ensure rat is not stuck in the middle
    val middleRow = rows / 2
    val middleCol = cols / 2
    grid(middleRow)(middleCol - 1) = true
    grid(middleRow)(middleCol) = true
    grid(middleRow - 1)(middleCol) = true

    grid
  }


  // prints the maze with start and end points. Uncomment line 44 in Game.scala to use this function while playing the game.
  def printMazeWithStartAndEnd(grid: Array[Array[Boolean]], start: Cell, end: Cell): Unit = {
    grid.indices.foreach { row =>
      grid(row).indices.foreach { col =>
        if (row == start.row && col == start.col) {
          print("S") 
        } else if (row == end.row && col == end.col) {
          print("E") 
        } else {
          if (grid(row)(col)) print("_") else print("#")
        }
      }
      println()
    }
  }
}
