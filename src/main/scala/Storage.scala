import java.io.PrintWriter
import scala.io.Source

class Storage:
  // function readMazeData takes the name of a file, and returns a maze if the file stores a valid maze.
  def readMazeData(fileName: String): Maze =
    val source = Source.fromFile(fileName)
    val lines = try source.getLines().toList finally source.close()

    val startIdx = lines.indexWhere(_.startsWith("Maze Object")) // ignoring all text which is not b/w "Maze object" and "End"
    val endIdx = lines.indexWhere(_.startsWith("End"), startIdx)

    if (startIdx != -1 && endIdx != -1) {
      val mazeLines = lines.slice(startIdx + 1, endIdx)

      val dimensionsPattern = """Dimensions: (\d+),(\d+)""".r // regex patterns to identify the properties of the maze object.
      val passagesPattern = """Passages: \(\((.*)\)\)""".r
      val wallsPattern = """Walls: \(\((.*)\)\)""".r
      val bridgesPattern = """Bridges: \((.*)\)""".r
      val highscorePattern = """Highscore: \((.*),(\d+)\)""".r

      // Pattern matching done where it is observed first, hense used collectFirst. Subsequent occurences are ignored.

      val dimensions = mazeLines.collectFirst { case dimensionsPattern(len, wid) => (len.toInt, wid.toInt) } // parsing the dimensions

      val passages = mazeLines.collectFirst {
        case passagesPattern(passagesStr) =>
          val passageList = passagesStr.split("\\), \\(").map { pairStr => //removing the brackets and mapping to Array of Passages.
            val pair = pairStr.replaceAll("[()]", "").split(",").map(_.toInt)
            Passage(pair(0), pair(1)) // arrays consist of consecutive coordinates.
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


      val bridges = mazeLines.collectFirst {
        case bridgesPattern(bridgesStr) =>
          val bridgeRegex = """\((\d+),(\d+)\)""".r // regex pattern for consecutive coordinates.
          val bridgeMatches = bridgeRegex.findAllMatchIn(bridgesStr) // find all occurences of pairs of coordinates

          val passages = bridgeMatches.map { matchResult =>
            val x = matchResult.group(1).toInt
            val y = matchResult.group(2).toInt
            new Passage(x, y)
          }.toArray // creating passages from coordinates

          passages.sliding(2, 2).map { case Array(p1, p2) =>
            new Bridge(p1, p2)
          }.toArray // creating bridges from consecutive passages,

      }


      val highscore = mazeLines.collectFirst {
        case highscorePattern(status, score) => (status, score.toInt) // parsed highscore
      }

      (dimensions, passages, walls, bridges, highscore) match {
        case (Some((len, wid)), Some(passagesList), Some(wallsList), Some(bridgesList), Some((status, score))) =>
          new Maze(len, wid, passagesList, wallsList, bridgesList, (status, score)) // if Maze properties exist, return maze
        case _ =>
          throw new IllegalArgumentException("Invalid maze data") // otherwise throw an error.
      }
    } else {
      throw new IllegalArgumentException("Invalid maze data format. File may have been currupted.")
    }
  end readMazeData


  // method writeMazeData takes a maze object and stores it into a file of a given na,e.
  def writeMazeData(maze: Maze, fileName: String): Unit =
    val mazeData = new PrintWriter(fileName)

    mazeData.write("Maze Object\n") // unique identifier for Maze objects
    // Following properties are stored in the text file:
    mazeData.write(s"Dimensions: ${maze.len},${maze.wid}\n")
    mazeData.write(s"Passages: ${maze.passagesAsString()}\n")
    mazeData.write(s"Walls: ${maze.wallsAsString()}\n")
    mazeData.write(s"Bridges: ${maze.bridgeAsString()}\n")
    mazeData.write("Highscore: " + maze.highscore.toString() + "\n")
    mazeData.write("End") // end of a maze object.

    mazeData.close()
  end writeMazeData

end Storage

