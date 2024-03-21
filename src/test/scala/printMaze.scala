
// test to check maze generation.
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

object hello extends App:
  printMazeWithStartAndEnd(MazeGenerator.generateMaze(20,20), Cell(10,10), Cell(19,19) )
