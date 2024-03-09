class Passage(val x: Int, val y: Int) extends Cell(x,y){
  override def toString: String = s"(${x},${y})"
}
