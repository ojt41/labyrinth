case class Cell(row: Int, col: Int) {
  override def toString: String = s"(${row},${col})"

  // calculating the neighbors. 
  def neighbors(grid: Array[Array[Boolean]]): List[Cell] = {
    val distance = 2
    val possibleNeighbors = List(
      (row - distance, col), // Top
      (row + distance, col), // Bottom
      (row, col - distance), //Left
      (row, col + distance)) // Right)

    possibleNeighbors
      .filter { case (r, c) =>
        r >= 0 && r < grid.length &&
          c >= 0 && c < grid(0).length &&
          !grid(r)(c)
      }
      .map { case (r, c) => Cell(r, c) }
  }

  // Calculating valid passages
  def valid(grid: Array[Array[Boolean]]): Boolean = {
    row >= 0 && row < grid.length &&
      col >= 0 && col < grid(0).length &&
      !grid(row)(col)
  }
}