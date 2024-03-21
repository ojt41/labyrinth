class Bridge(val entrance1: Passage, val entrance2: Passage) {
  def printBridge() = println(s"bridge is $entrance1, $entrance2")

  override def toString: String = s"${entrance1}, ${entrance2}"
}
