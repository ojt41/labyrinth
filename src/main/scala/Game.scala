import MazeGenerator.printMazeWithStartAndEnd
import scala.collection.mutable
import scala.util.Random

class Game(val rat: Rat, val storage: Storage) {

  // Creates a new maze object
  def newMaze(len: Int, wid: Int): Maze = {
    val rows = len
    val cols = wid
    // use MazeGenerator to create grid
    val grid = MazeGenerator.generateMaze(rows, cols)

    // extract passages and walls from grid
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

    // Create bridges by connecting random pairs of passages
    val bridgesE = mutable.Buffer[Bridge]()
    var i = 0
    val randomPassage = Random.shuffle(passages)

    // found dividing by 4 to generate bridges consistently based on maze dimension.
    while i < (rows + cols) / 4 do
      if randomPassage(i).row != randomPassage(i + 1).row && randomPassage(i).col != randomPassage(i + 1).col then
        // should not be in the same row and column
        bridgesE.append(new Bridge(randomPassage(i), randomPassage(i + 1)))
      i += 2

    val bridges = bridgesE.take(6).toArray // take at max 6 bridges


    val newMazeReturn = new Maze(len, wid, passages.toArray, walls.toArray, bridges)

    // defining start and end point for maze
    val start = Cell(rows / 2, cols / 2)
    val end = Cell(rows - 1, cols - 1)
    // Uncomment the following line to print the maze.
    //printMazeWithStartAndEnd(grid, start, end)

    newMazeReturn
  }

  // initiliase rat in maze at starting point
  def startGame(maze: Maze): Unit = {
    val start = Passage(maze.len / 2, maze.wid / 2)
    rat.currentPos = start
  }

  // check if game ended.
  def endGame(maze: Maze, anotherRat: Rat): Boolean = {
    val end = Cell(maze.len - 1, maze.wid - 1)
    rat.currentPos == end || anotherRat.currentPos == rat.currentPos
  }
}

