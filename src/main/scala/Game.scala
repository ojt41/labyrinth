import scala.collection.mutable
import scala.util.Random

class Game(val rat: Rat, val timer: Timer, val storage: Storage ) {

  def newMaze(len: Int, wid: Int): Maze = {
    val rows = len
    val cols = wid
    val grid = MazeGenerator.generateMaze(rows, cols)

    val passages = for {
      row <- 0 until rows
      col <- 0 until cols
      if grid(row)(col)
    } yield Passage(row, col)

    val walls = for {
      row <- 0 until rows
      col <- 0 until cols
      if !grid(row)(col)
    } yield Wall(row, col)


    val bridgesE = mutable.Buffer[Bridge]()
    var i = 0
    val randomPassage = Random.shuffle(passages)

    while i < (rows + cols) / 4 do
      if randomPassage(i).row != randomPassage(i+1).row && randomPassage(i).col != randomPassage(i+1).col then
        bridgesE.append(new Bridge(randomPassage(i), randomPassage(i+1)))
      i += 2

    val bridges = bridgesE.toArray


    val newMazeReturn = new Maze(len, wid, passages.toArray, walls.toArray, bridges)

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
    val start = Cell(rows / 2, cols / 2)
    val end = Cell(rows - 1, cols - 1)
    printMazeWithStartAndEnd(grid, start, end)

    newMazeReturn
  }

  def startGame(maze: Maze): Unit = {
    // Implement game start logic

    val start = Passage(maze.len / 2, maze.wid / 2)
    rat.currentPos = start
  }

  def endGame(maze: Maze): Unit = {
    val end = Cell(maze.len - 1, maze.wid - 1)
    if rat.currentPos == end then
      println("game over")
  }
}

object hello extends App:
  val hell = Game(new Rat(Passage(0,0)), new Timer, new Storage)

  val newMaze = hell.newMaze(21, 22)

  hell.startGame(newMaze)

  println(newMaze)
  println(newMaze.passages.mkString("Array(", ", ", ")"))
  //newMaze.bridges.foreach(_.printBridge())
  println(newMaze.solveMaze(hell.rat).mkString("Array(", ", ", ")"))
