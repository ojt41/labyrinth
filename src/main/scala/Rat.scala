class Rat(var currentPos: Passage) {
  def moveUp(maze: Maze): Unit = {
    val newPos = Passage(currentPos.row - 1, currentPos.col)
    if (maze.hasPassage(newPos) || maze.hasBridge(currentPos, newPos)) currentPos = newPos
  }

  def moveDown(maze: Maze): Unit = {
    val newPos = Passage(currentPos.row + 1, currentPos.col)
    if (maze.hasPassage(newPos) || maze.hasBridge(currentPos, newPos)) currentPos = newPos
  }

  def moveLeft(maze: Maze): Unit = {
    val newPos = Passage(currentPos.row, currentPos.col - 1)
    if (maze.hasPassage(newPos) || maze.hasBridge(currentPos, newPos)) currentPos = newPos
  }

  def moveRight(maze: Maze): Unit = {
    val newPos = Passage(currentPos.row, currentPos.col + 1)
    if (maze.hasPassage(newPos) || maze.hasBridge(currentPos, newPos)) currentPos = newPos
  }

  def moveToOtherEnd(maze: Maze): Unit = {
    maze.bridges.find(bridge => bridge.entrance1 == currentPos || bridge.entrance2 == currentPos) match {
      case Some(bridge) =>
        currentPos = if (currentPos == bridge.entrance1) bridge.entrance2 else bridge.entrance1
      case None => 
    }
  }
}
