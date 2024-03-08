import java.io.PrintWriter


class Storage {
  def readMazeData(fileName: String): Maze = ???

  def writeMazeData(maze: Maze, fileName: String): Unit =
    val mazeData = new PrintWriter(fileName)
    mazeData.write("Maze Object\n")

    mazeData.write(s"Dimensions: ${maze.len},${maze.wid}\n")
    mazeData.write(s"Passages: ${maze.passagesAsString()}\n")
    mazeData.write(s"Walls: ${maze.wallsAsString()}\n")
    mazeData.write(s"Bridges: ${maze.bridgeAsString()}\n")
    mazeData.write("Highscore: " + maze.highscore.toString() + "\n")

    mazeData.write("End")

    mazeData.close()
}


//class Maze(val len: Int, val wid: Int, val passages: Array[Passage],
// val walls: Array[Wall],  val bridges: Array[Bridge],
// var highscore: (String, Int) = ("Not solved", 99999))

object tests extends App{
  val hell = Game(new Rat(Passage(0,0)), new Storage)

  val newMaze = hell.newMaze(21, 22)
  hell.startGame(newMaze)
  hell.storage.writeMazeData(newMaze, "storageWrite_testRun_8march.txt")


}

