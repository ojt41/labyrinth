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

object testWrite extends App{
  val game = Game(new Rat(Passage(0,0)), new Storage)

  val newMaze = game.newMaze(21, 22)
  game.startGame(newMaze)
  println(newMaze)
  println(newMaze.highscore)
  println(newMaze.bridgeAsString())
  println(newMaze.wallsAsString())
  println(newMaze.passagesAsString())
  game.storage.writeMazeData(newMaze, "storageWrite_testRun_8march.txt")
}


object testRead extends App{
  val game = Game(new Rat(Passage(0,0)), new Storage)
  val mazegen = MazeGenerator
  val newMaze = game.storage.readMazeData("storageWrite_testRun_8march.txt")
  game.startGame(newMaze)
  println(newMaze)
  println(newMaze.highscore)
  println(newMaze.bridgeAsString())
  println(newMaze.wallsAsString())
  println(newMaze.passagesAsString())

}

