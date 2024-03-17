import org.scalatest.funsuite.AnyFunSuite


//These are tests for the storage class, I tried to generate mazes with writeMaze method of storage class and
//store it in a arbitrary file, and compare with the result on using readMaze data's given Maze object.


class StorageTest extends AnyFunSuite {

  test("Test written maze is equal to read maze") {
    val game = Game(new Rat(Passage(0, 0)), new Storage)

    val writtenMaze = game.newMaze(20, 20) // testing for square maze. You can edit this to check for any possible length and breadth.
    game.storage.writeMazeData(writtenMaze, "storageWrite_testRun_9march.txt")

    val readMaze = game.storage.readMazeData("storageWrite_testRun_9march.txt")

    // Assert maze properties are equal
    assert(writtenMaze.toString == readMaze.toString)
    assert(writtenMaze.highscore == readMaze.highscore)
    assert(writtenMaze.bridgeAsString() == readMaze.bridgeAsString())
    assert(writtenMaze.wallsAsString() == readMaze.wallsAsString())
    assert(writtenMaze.passagesAsString() == readMaze.passagesAsString())
  }

  // This tests for 1000 randomly selected dimensions for the maze in range 10 to 200.
  test("Test 1000 random mazes") {
    val game = Game(new Rat(Passage(0, 0)), new Storage)

    for (x <- 1 to 1000) {
      // Generate random sizes between 10 and 200 inclusive
      val randomLength = scala.util.Random.between(10, 201)
      val randomWidth = scala.util.Random.between(10, 201)

      // Create and write the maze
      val writtenMaze = game.newMaze(randomLength, randomWidth)
      game.storage.writeMazeData(writtenMaze, "x.txt")

      // Read the maze
      val readMaze = game.storage.readMazeData("x.txt")

      // Assert maze properties are equal
      assert(writtenMaze.toString == readMaze.toString)
      assert(writtenMaze.highscore == readMaze.highscore)
      assert(writtenMaze.bridgeAsString() == readMaze.bridgeAsString())
      assert(writtenMaze.wallsAsString() == readMaze.wallsAsString())
      assert(writtenMaze.passagesAsString() == readMaze.passagesAsString())
    }
  }


  test("Test load non-existent maze") {
    val game = Game(new Rat(Passage(0, 0)), new Storage)
    val nonExistentFile = "non_existent_maze.txt"
    // Try to load a non-existent maze
    var testResult: String = ""
    try {
      val loadedMaze = game.storage.readMazeData(nonExistentFile)
      testResult = "Test failed"
    } catch {
      case _: Throwable => testResult = "Passed"
    }
    assert(testResult == "Passed")
  }
}


object testWrite extends App {
  val game = Game(new Rat(Passage(0, 0)), new Storage)
  val writtenMaze = game.newMaze(20, 19)
  game.startGame(writtenMaze)
  println(writtenMaze)
  println(writtenMaze.highscore)
  println(writtenMaze.bridgeAsString())
  println(writtenMaze.wallsAsString())
  println(writtenMaze.passagesAsString())
  game.storage.writeMazeData(writtenMaze, "storageWrite_testRun_9march.txt")
}


object testRead extends App {
  val game = Game(new Rat(Passage(0, 0)), new Storage)
  val mazegen = MazeGenerator
  val readMaze = game.storage.readMazeData("storageWrite_testRun_9march.txt")
  game.startGame(readMaze)
  println(readMaze)
  println(readMaze.highscore)
  println(readMaze.bridgeAsString())
  println(readMaze.wallsAsString())
  println(readMaze.passagesAsString())
}