import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.canvas.{Canvas, GraphicsContext}
import scalafx.scene.input.KeyEvent
import scalafx.scene.layout.BorderPane
import scalafx.scene.paint.Color
import scalafx.Includes._
import scalafx.scene.input.KeyCode
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.animation.AnimationTimer
import scalafx.scene.control.TextInputDialog

object MazeGUI extends JFXApp3 {

  var length: Int = _
  var mazeWid: Int = _

  val game = new Game(new Rat(Passage(length / 2, mazeWid / 2)), new Timer, new Storage)
  var maze: Maze = _
  var rat: Rat = _

  var highlightSolution: Boolean = false
  var solution: Array[Passage] = Array.empty

  var startTime: Long = 0
  var elapsedTime: Long = 0
  var timerRunning: Boolean = false

  def solveAndHighlight(): Unit = {
    solution = maze.solveMaze(rat)
  }

  def showVictoryMessage(): Unit = {
    val alert = new Alert(AlertType.Information)
    alert.title = "Congratulations!"
    alert.headerText = "Victory!"
    alert.contentText = s"You have reached the exit in ${elapsedTime/ 1e9} seconds. Congratulations on solving the maze!"
    alert.showAndWait()
  }

  val timer = AnimationTimer(time => {
    if (timerRunning) {
      elapsedTime = time - startTime
    }
  })

  override def start(): Unit = {
    val dialogLength = new TextInputDialog() {
      title = "Enter Length"
      headerText = "Enter the length of maze:"
    }

    val dialogWidth = new TextInputDialog() {
      title = "Enter Width"
      headerText = "Enter the width of the maze:"
    }

    val lengthResult = dialogLength.showAndWait()
    val widthResult = dialogWidth.showAndWait()

    length = lengthResult match {
      case Some("") => 30
      case Some(input) => input.toInt
      case None => 30
    }

    mazeWid = widthResult match {
      case Some("") => 30
      case Some(input) => input.toInt
      case None => 30
    }

    maze = game.newMaze(length, mazeWid)
    rat = game.rat
    game.startGame(maze)

    val canvasWidth = maze.wid * 20
    val canvasHeight = maze.len * 20

    val canvas = new Canvas(canvasWidth, canvasHeight)
    val gc: GraphicsContext = canvas.graphicsContext2D

    def drawMaze(): Unit = {
      gc.fill = Color.Black
      gc.fillRect(0, 0, canvasWidth, canvasHeight)

      gc.fill = Color.White
      maze.passages.foreach { passage =>
        val x = passage.col * 20
        val y = passage.row * 20
        gc.fillRect(x, y, 20, 20)
      }

      gc.fill = Color.Blue // Rat color
      val ratX = rat.currentPos.col * 20
      val ratY = rat.currentPos.row * 20
      gc.fillRect(ratX, ratY, 20, 20)

      gc.fill = Color.Green
      val endX = maze.wid * 20 - 20
      val endY = maze.len * 20 - 20
      gc.fillRect(endX, endY, 20, 20)

      gc.setStroke(Color.Red)
      gc.setLineWidth(3)

      maze.bridges.foreach { bridge =>
        val entrance1 = bridge.entrance1
        val entrance2 = bridge.entrance2
        val x1 = entrance1.col * 20 + 10
        val y1 = entrance1.row * 20 + 10
        val x2 = entrance2.col * 20 + 10
        val y2 = entrance2.row * 20 + 10

        if (highlightSolution && (solution.contains(entrance1)) && (solution.contains(entrance2))) then {
          gc.setStroke(Color.Green)
        } else {
          gc.setStroke(Color.Red)
        }

        gc.strokeLine(x1, y1, x2, y2)
      }

      if (highlightSolution) {
        gc.setStroke(Color.Green)
        gc.setLineWidth(2)
        solution.foreach { passage =>
          val x = passage.col * 20 + 10
          val y = passage.row * 20 + 10
          gc.strokeOval(x, y, 5, 5)
        }
      }

      gc.setStroke(Color.DarkMagenta)
      gc.setLineWidth(1)
      gc.strokeText(s"Time: ${elapsedTime / 1e9} seconds", 10, canvasHeight - 10)
    }

    drawMaze()

    canvas.requestFocus()
    canvas.focusTraversable = true

    canvas.onKeyPressed = (event: KeyEvent) => {
      event.code match {
        case KeyCode.Up =>
          if (!timerRunning) {
            startTime = System.nanoTime()
            timerRunning = true
            timer.start()
          }
          rat.moveUp(maze)
        case KeyCode.Down =>
          if (!timerRunning) {
            startTime = System.nanoTime()
            timerRunning = true
            timer.start()
          }
          rat.moveDown(maze)
        case KeyCode.Left =>
          if (!timerRunning) {
            startTime = System.nanoTime()
            timerRunning = true
            timer.start()
          }
          rat.moveLeft(maze)
        case KeyCode.Right =>
          if (!timerRunning) {
            startTime = System.nanoTime()
            timerRunning = true
            timer.start()
          }
          rat.moveRight(maze)
        case KeyCode.Space =>
          if (!timerRunning) {
            startTime = System.nanoTime()
            timerRunning = true
            timer.start()
          }
          rat.moveToOtherEnd(maze)
        case KeyCode.H =>
          highlightSolution = true
          solveAndHighlight()
        case _ =>
          timerRunning = true
      }

      if (rat.currentPos == Passage(maze.len - 1, maze.wid - 1)) {
        timer.stop()
        timerRunning = false
        showVictoryMessage()
      }

      drawMaze()
    }

    canvas.onKeyReleased = (event: KeyEvent) => {
      event.code match {
        case KeyCode.H =>
          highlightSolution = false
          drawMaze()
        case _ =>
      }
    }

    val root = new BorderPane
    root.center = canvas

    val mainScene = new Scene(
      root, canvasWidth, canvasHeight, Color.rgb(20, 20, 20))

    stage = new JFXApp3.PrimaryStage {
      title = "lost in maze"
      width = canvasWidth + 20
      height = canvasHeight + 100
      scene = mainScene
    }
  }
}
