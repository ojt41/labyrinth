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

    // recursive function to generate maze
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

  // function to generate random bridges between passages
  def generateBridges(rows: Int, cols: Int): Array[Bridge] = {
    val bridges = mutable.Buffer[Bridge]() // stores generated bridges
    val passages = for {
      row <- 0 until rows
      col <- 0 until cols
    } yield Passage(row, col)

    val randomPassages = Random.shuffle(passages)

    var i = 0
    while (i < randomPassages.length - 3) {
      val passage1 = randomPassages(i)
      val passage2 = randomPassages(i + 1)
      val passage3 = randomPassages(i + 2)
      val passage4 = randomPassages(i + 3)

      // check if the passages are not in the same row or column to eliminate redundant bridges
      if (passage1.row != passage2.row && passage1.col != passage2.col &&
          passage3.row != passage4.row && passage3.col != passage4.col) {
        val bridge1 = Bridge(passage1, passage2)
        val bridge2 = Bridge(passage3, passage4)

        // checking if bridges are overlapping
        val overlaps = bridges.exists(existingBridge =>
          (existingBridge.entrance1 == bridge1.entrance1 && existingBridge.entrance2 == bridge1.entrance2) ||
          (existingBridge.entrance1 == bridge2.entrance1 && existingBridge.entrance2 == bridge2.entrance2) ||
          (existingBridge.entrance1 == bridge1.entrance2 && existingBridge.entrance2 == bridge1.entrance1) ||
          (existingBridge.entrance1 == bridge2.entrance2 && existingBridge.entrance2 == bridge2.entrance1)
        )
        
        // if bridges don't overlap, add them to buffer.
        if (!overlaps) {
          bridges.append(bridge1)
          bridges.append(bridge2)
          i += 4
        } else {
          i += 1
        }
      } else {
        i += 1
      }
    }
    bridges.toArray
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
