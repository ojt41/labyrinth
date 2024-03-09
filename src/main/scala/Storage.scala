import java.io.PrintWriter



import scala.io.Source


class Storage:
  def readMazeData(fileName: String): Maze =
    val source = Source.fromFile(fileName)
    val lines = try source.getLines().toList finally source.close()

    val startIdx = lines.indexWhere(_.startsWith("Maze Object"))
    val endIdx = lines.indexWhere(_.startsWith("End"), startIdx)

    if (startIdx != -1 && endIdx != -1) {
      val mazeLines = lines.slice(startIdx + 1, endIdx)

      val dimensionsPattern = """Dimensions: (\d+),(\d+)""".r
      val passagesPattern = """Passages: \(\((.*)\)\)""".r
      val wallsPattern = """Walls: \(\((.*)\)\)""".r
      val bridgesPattern = """Bridges: \((.*)\)""".r
      val highscorePattern = """Highscore: \((.*),(\d+)\)""".r

      val dimensions = mazeLines.collectFirst {case dimensionsPattern(len, wid)=> (len.toInt, wid.toInt)}

      val passages = mazeLines.collectFirst {
        case passagesPattern(passagesStr)=>
          val passageList = passagesStr.split("\\), \\(").map { pairStr =>
            val pair = pairStr.replaceAll("[()]", "").split(",").map(_.toInt)
            Passage(pair(0),pair(1))
          }
          passageList
      }

      val walls = mazeLines.collectFirst {
        case wallsPattern(wallsStr) => {
          val wallsList = wallsStr.split("\\), \\(").map { pairStr =>
            val pair = pairStr.replaceAll("[()]", "").split(",").map(_.toInt)
            Wall(pair(0), pair(1))
          }
          wallsList
        }
      }

      //mazeLines.collectFirst{case bridgesPattern(bridgesStr) => println(bridgesStr)}

      val bridges = mazeLines.collectFirst {
        case bridgesPattern(bridgesStr) =>
          val bridgeRegex = """\((\d+),(\d+)\)""" .r
          val bridgeMatches = bridgeRegex.findAllMatchIn(bridgesStr)

          val passages = bridgeMatches.map{ matchResult =>
            val x = matchResult.group(1).toInt
            val y = matchResult.group(2).toInt
            new Passage(x, y)
          }.toArray

          passages.sliding(2, 2).map{case Array(p1, p2) =>
            new Bridge(p1, p2)
          }.toArray

      }


      val highscore = mazeLines.collectFirst {
        case highscorePattern(status, score) => (status, score.toInt)
      }

      (dimensions, passages, walls, bridges, highscore) match {
        case (Some((len, wid)), Some(passagesList), Some(wallsList), Some(bridgesList) , Some((status, score)))=>
          new Maze(len, wid, passagesList, wallsList, bridgesList, (status, score))
        case _ =>
          throw new IllegalArgumentException("Invalid maze data")
      }
    } else{
        throw new IllegalArgumentException("Invalid maze data format. File may have been currupted.")
    }
  end readMazeData


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

  end writeMazeData


end Storage

//class Maze(val len: Int, val wid: Int, val passages: Array[Passage],
// val walls: Array[Wall],  val bridges: Array[Bridge],
// var highscore: (String, Int) = ("Not solved", 99999))

object testWrite extends App{
  val game = Game(new Rat(Passage(0,0)), new Storage)

  val newMaze = game.newMaze(20, 19)
  game.startGame(newMaze)
  println(newMaze)
  println(newMaze.highscore)
  println(newMaze.bridgeAsString())
  println(newMaze.wallsAsString())
  println(newMaze.passagesAsString())
  game.storage.writeMazeData(newMaze, "storageWrite_testRun_9march.txt")
}


object testRead extends App{
  val game = Game(new Rat(Passage(0,0)), new Storage)
  val mazegen = MazeGenerator
  val newMaze = game.storage.readMazeData("storageWrite_testRun_9march.txt")
  game.startGame(newMaze)
  println(newMaze)
  println(newMaze.highscore)
  println(newMaze.bridgeAsString())
  println(newMaze.wallsAsString())
  println(newMaze.passagesAsString())

}

