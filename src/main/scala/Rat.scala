class Rat(var currentPos: Passage) {
  def moveUp(passages: Seq[Passage]): Unit = {
    val newPos = Passage(currentPos.row - 1, currentPos.col)
    if (passages.contains(newPos)) currentPos = newPos
  }

  def moveDown(passages: Seq[Passage]): Unit = {
    val newPos = Passage(currentPos.row + 1, currentPos.col)
    if (passages.contains(newPos)) currentPos = newPos
  }

  def moveLeft(passages: Seq[Passage]): Unit = {
    val newPos = Passage(currentPos.row, currentPos.col - 1)
    if (passages.contains(newPos)) currentPos = newPos
  }

  def moveRight(passages: Seq[Passage]): Unit = {
    val newPos = Passage(currentPos.row, currentPos.col + 1)
    if (passages.contains(newPos)) currentPos = newPos
  }
}