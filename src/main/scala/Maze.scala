import scala.collection.mutable
import scala.util.Random

class Maze(val len: Int, val wid: Int, val passages: Array[Passage], val walls: Array[Wall],  val bridges: Array[Bridge], var highscore: (String, Int) = ("Not solved", 99999)) {


  override def toString: String = s"$len, $wid created"

  def passagesAsString(): String = this.passages.mkString("(", ", ", ")")

  def wallsAsString(): String = this.walls.mkString("(", ", ", ")")

  def bridgeAsString(): String = this.bridges.mkString("(", ", ", ")")


  def solveMaze(rat: Rat): Array[Passage] = {
    val start = rat.currentPos
    val end = Passage(this.len - 1, this.wid - 1)
    /*println(start)
    println(end)*/
    val queue = mutable.Queue[Passage]()
    val visited = mutable.Set[Passage]()
    val howToGet = mutable.Map[Passage, Passage]()
    howToGet += start -> start

    queue.enqueue(start)

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

    val path = mutable.Buffer[Passage]()
    var key = end
    path.append(key)

    while (key != start) {
      val value = howToGet(key)
      path.append(value)
      key = value
    }

    path.reverse.toArray
  }
  def possiblePassages(passage: Passage): List[Passage] = {
    val neighbors = List(
      Passage(passage.row, passage.col - 1), // Left
      Passage(passage.row, passage.col + 1), // Right
      Passage(passage.row - 1, passage.col), // Up
      Passage(passage.row + 1, passage.col)  // Down
    )

    val bridges = this.bridges.filter(bridge =>
      (bridge.entrance1==passage)||(bridge.entrance2 == passage)
    )

    val bridgePassages = bridges.flatMap{bridge =>
      if (bridge.entrance1 == passage) then {
        List(bridge.entrance2)
      } else {
        List(bridge.entrance1)
      }
    }

    (neighbors ++ bridgePassages).filter(validPassage)
  }

  private def validPassage(passage: Passage): Boolean =
    (passage.row >= 0) && {passage.row < len && passage.col >= 0
    } && (passage.col < wid && (hasPassage(passage)) || (hasBridge(passage, passage)))

  def updateHighScore(player: String, movesTaken: Int): Unit =
    highscore = (player, movesTaken)

  def hasPassage(cell: Cell): Boolean =
    passages.exists(p => p.row == cell.row && p.col == cell.col)

  def hasBridge(cell1: Cell, cell2: Cell): Boolean =
    bridges.exists(b => (b.entrance1 == cell1 && b.entrance2 == cell2) || (b.entrance1 == cell2 && b.entrance2 == cell1))

}



 /* def possibleMoves(cell: Cell): List[Cell] = {
    val neighbors = List(
      Cell(cell.row, cell.col - 1), // Left
      Cell(cell.row, cell.col + 1), // Right
      Cell(cell.row - 1, cell.col), // Up
      Cell(cell.row + 1, cell.col)  // Down
    )

    neighbors.filter(validMove)
  }

  private def validMove(cell: Cell): Boolean =
    cell.row >= 0 && cell.row < len && cell.col >= 0 && cell.col < wid && (hasPassage(cell) || hasBridge(cell, cell))

}*/
