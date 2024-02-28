import scala.collection.mutable
import scala.util.Random

object MazeGenerator {

  def generateMaze(rows: Int, cols: Int): Array[Array[Boolean]] = {
    val grid = Array.ofDim[Boolean](rows, cols)
    val stack = mutable.Stack[Cell]()
    val rand = new Random()

    // Set the starting point to the middle
    val startRow = rows / 2
    val startCol = cols / 2
    val startCell = Cell(startRow, startCol)

    def markVisited(cell: Cell): Unit = {
      grid(cell.row)(cell.col) = true
    }

    def allVisited(cell: Cell): Boolean = {
      cell.neighbors(grid).forall(n =>
        grid(n.row)(n.col))
    }

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

    generate(startCell)

    // Open the exit at the bottom right
    grid(rows - 1)(cols - 2) = true
    grid(rows - 2)(cols - 1) = true
    grid(rows - 1)(cols - 1) = true

    grid
  }


  def printMazeWithStartAndEnd(grid: Array[Array[Boolean]], start: Cell, end: Cell): Unit = {
    grid.indices.foreach { row =>
      grid(row).indices.foreach { col =>
        if (row == start.row && col == start.col) {
          print("S") // Print 'S' for start
        } else if (row == end.row && col == end.col) {
          print("E") // Print 'E' for end
        } else {
          if (grid(row)(col)) print("_") else print("#")
        }
      }
      println()
    }
  }

  def main(args: Array[String]): Unit = {
    val rows = 10
    val cols = 10
    val maze = generateMaze(rows, cols)

    // Define the start and end points
    val start = Cell(rows / 2, cols / 2)
    val end = Cell(rows - 1, cols - 1)

    printMazeWithStartAndEnd(maze, start, end)
  }

}
