class Passage(x: Int,y: Int) extends Cell(x,y){
  override def toString: String = s"(${x},${y})"
}
