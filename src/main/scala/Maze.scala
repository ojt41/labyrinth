import scala.collection.mutable
import scala.util.Random

class Maze(val len: Int, val wid: Int, val passages: Array[Passage], val walls: Array[Wall],  val bridges: Array[Bridge]) {

  var highscore: (String, Int) = ("Not solved", 99999)

  override def toString: String = s"$len, $wid created"

  def solveMaze(rat: Rat): Array[Passage] = {
    val start = rat.currentPos
    val end = Passage(this.len - 1, this.wid - 1)
    println(start)
    println(end)
    val stack = mutable.Stack[Passage]()
    val visited = mutable.ArrayBuffer[Passage]()
    val howToGet = mutable.Map[Passage, Passage]()
    howToGet += start -> start

    stack.append(start)

    while (stack.nonEmpty) do {
      var current = stack.head

      var passageLeft = Passage(current.row, current.col - 1)
      if passages.contains(passageLeft) && !visited.contains(passageLeft) then {
        stack.append(passageLeft)
        howToGet += passageLeft -> current
      }


      var passageRight = Passage(current.row, current.col + 1)
      if passages.contains(passageRight) && !visited.contains(passageRight) then {
        stack.append(passageRight)
        howToGet += passageRight -> current
      }

      var passageUp = Passage(current.row - 1, current.col)
      if passages.contains(passageUp) && !visited.contains(passageUp) then {
        stack.append(passageUp)
        howToGet += passageUp -> current
      }

      var passageDown = Passage(current.row + 1, current.col)
      if passages.contains(passageDown) && !visited.contains(passageDown) then {
        stack.append(passageDown)
        howToGet += passageDown -> current
      }

      visited.append(current)
      stack.pop()


    }

    //howToGet.foreach(println(_))

    println(s"${howToGet(end)} key")

    val path = mutable.Buffer[Passage]()
    var key = end
    path.append(key)

    while key!= start do
      var value = howToGet(key)
      path.append(value)
      key = value





    path.reverse.toArray
  }

  def updateHighScore(player: String, timeTaken: Int): Unit =
    highscore = (player, timeTaken)

  def hasPassage(cell: Cell): Boolean =
    passages.exists(p => p.row == cell.row && p.col == cell.col)

  def hasBridge(cell1: Cell, cell2: Cell): Boolean =
    bridges.exists(b => (b.entrance1 == cell1 && b.entrance2 == cell2) || (b.entrance1 == cell2 && b.entrance2 == cell1))

  def possibleMoves(cell: Cell): List[Cell] = {
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

}
