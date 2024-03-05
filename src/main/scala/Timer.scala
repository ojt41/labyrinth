case class Timer() {
  var time: Int = 0

  def update(): Unit = {
    time += 1
  }

  def reset(): Unit = {
    time = 0
  }

  def start(): Unit = {
    time = 0
  }

  def stop(): Unit = {
  }
}
