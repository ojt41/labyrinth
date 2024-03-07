import java.io.PrintWriter


class Storage {
  def readMazeData(fileName: String): Maze = ???
  def writeMazeData(maze: Maze, fileName: String): Unit =
    val mazeData = new PrintWriter(fileName)
    mazeData.write(s"${maze.len} ${maze.wid}")
    mazeData.close()
}

object tests extends App{
  val hell = Game(new Rat(Passage(0,0)), new Storage)

  val newMaze = hell.newMaze(21, 22)
  hell.startGame(newMaze)

  hell.storage.writeMazeData(newMaze, "test.txt")


}

