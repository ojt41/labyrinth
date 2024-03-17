//These are tests for the storage class, I tried to generate a maze with testWrite object and
//store it in a arbitrary directory, along with printing.

//import org.scalatest

object testWrite extends App{
  val game = Game(new Rat(Passage(0,0)), new Storage)
  val writtenMaze = game.newMaze(20, 19)
  game.startGame(writtenMaze)
  println(writtenMaze)
  println(writtenMaze.highscore)
  println(writtenMaze.bridgeAsString())
  println(writtenMaze.wallsAsString())
  println(writtenMaze.passagesAsString())
  game.storage.writeMazeData(writtenMaze, "storageWrite_testRun_9march.txt")
}


object testRead extends App{
  val game = Game(new Rat(Passage(0,0)), new Storage)
  val mazegen = MazeGenerator
  val readMaze = game.storage.readMazeData("storageWrite_testRun_9march.txt")
  game.startGame(readMaze)
  println(readMaze)
  println(readMaze.highscore)
  println(readMaze.bridgeAsString())
  println(readMaze.wallsAsString())
  println(readMaze.passagesAsString())
}


