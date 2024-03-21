import scala.collection.mutable
import scala.util.Random

class Maze(val len: Int, val wid: Int, val passages: Array[Passage], val walls: Array[Wall],  val bridges: Array[Bridge], var highscore: (String, Int) = ("Anonymous", 99999)) {
  // the 4 following methods are for debugging.
  override def toString: String = s"$len, $wid created"

  def passagesAsString(): String = this.passages.mkString("(", ", ", ")")

  def wallsAsString(): String = this.walls.mkString("(", ", ", ")")

  def bridgeAsString(): String = this.bridges.mkString("(", ", ", ")")


  // method to solve maze using Breadth first search.
  // described in detail in Document
  def solveMaze(rat: Rat): Array[Passage] = {
    val start = rat.currentPos
    val end = Passage(this.len - 1, this.wid - 1)
    val queue = mutable.Queue[Passage]() // using mutable queue
    val visited = mutable.Set[Passage]()
    val howToGet = mutable.Map[Passage, Passage]()
    howToGet += start -> start

    queue.enqueue(start)

    // Perform BFS
    while (queue.nonEmpty) {
      val current = queue.dequeue()

      val neighbors = possiblePassages(current)
      neighbors.foreach { neighbor =>
        if !visited.contains(neighbor) then {
          queue.enqueue(neighbor)
          howToGet += neighbor -> current
          visited.add(neighbor)
        }
      }
    }

    // Reconstruct path from end to start
    val path = mutable.Buffer[Passage]()
    var key = end
    path.append(key)

    while (key != start) {
      val value = howToGet(key)
      path.append(value)
      key = value
    }

    // Reverse the path to get it from start to end
    path.reverse.toArray
  }
  
  // Finds possible passages from current passage (bridges included)
  def possiblePassages(passage: Passage): List[Passage] = {
    val neighbors = List(
      Passage(passage.row, passage.col - 1), // Left
      Passage(passage.row, passage.col + 1), // Right
      Passage(passage.row - 1, passage.col), // Up
      Passage(passage.row + 1, passage.col)  // Down
    )
    
    // filter bridges that start/end with this passage
    val bridges = this.bridges.filter(bridge =>
      (bridge.entrance1==passage)||(bridge.entrance2 == passage)
    )

    // find other end of bridge
    val bridgePassages = bridges.flatMap{bridge =>
      if (bridge.entrance1 == passage) then {
        List(bridge.entrance2)
      } else {
        List(bridge.entrance1)
      }
    }
    (neighbors ++ bridgePassages).filter(validPassage)
  }

  // method to check if passage is valid
  def validPassage(passage: Passage): Boolean =
    (passage.row >= 0) && {passage.row < len && passage.col >= 0
    } && (passage.col < wid && (hasPassage(passage)) || (hasBridge(passage, passage)))

  // method to update highscore
  def updateHighScore(player: String, movesTaken: Int): Unit =
    highscore = (player, movesTaken)

  // method to check if passage exists in the maze
  def hasPassage(cell: Cell): Boolean =
    passages.exists(p => p.row == cell.row && p.col == cell.col)

  // method to check if passages are connected via bridge
  def hasBridge(cell1: Cell, cell2: Cell): Boolean =
    bridges.exists(b => (b.entrance1 == cell1 && b.entrance2 == cell2) || (b.entrance1 == cell2 && b.entrance2 == cell1))

}




