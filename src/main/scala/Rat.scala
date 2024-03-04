case class Rat(var currentPos: Passage) {
  def moveUp(): Unit = {
    currentPos = Passage(currentPos.row - 1, currentPos.col)
  }

  def moveDown(): Unit = {
    currentPos = Passage(currentPos.row + 1, currentPos.col)
  }

  def moveLeft(): Unit = {
    currentPos = Passage(currentPos.row, currentPos.col - 1)
  }

  def moveRight(): Unit = {
    currentPos = Passage(currentPos.row, currentPos.col + 1)
  }
}
