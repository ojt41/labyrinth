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
import scalafx.scene.control.TextInputDialog


object MazeGUI extends JFXApp3 {

  var length: Int = _
  var mazeWid: Int = _

  val game = new Game(new Rat(Passage(length / 2, mazeWid / 2)), new Storage)
  var maze: Maze = _
  var rat: Rat = _

  var highlightSolution: Boolean = false
  var solution: Array[Passage] = Array.empty

  var helpUsed = false
  var movesTaken = 0

  def solveAndHighlight(): Unit = {
    solution = maze.solveMaze(rat)
  }

  def showVictoryMessage(): Unit = {
    val alert = new Alert(AlertType.Information)
    alert.title = if (!helpUsed) then "Congratulations!" else "Game Over"
    alert.headerText = if (!helpUsed) then "Victory!" else "Try to solve the maze on your own."
    
    alert.contentText = 
      if (!helpUsed) then 
        (s"You have reached the exit in ${movesTaken} moves. Congratulations on solving the maze!") 
      else 
        ("You didn't solve the maze on your own.")
    alert.showAndWait()
  }

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

    val scaleFactor = 800 / ((mazeWid + length)/2)
    val canvasWidth = scaleFactor * mazeWid
    val canvasHeight = scaleFactor * length

    val canvas = new Canvas(canvasWidth, canvasHeight)
    val gc: GraphicsContext = canvas.graphicsContext2D

    def drawMaze(): Unit = {
      gc.fill = Color.Black
      gc.fillRect(0, 0, canvasWidth, canvasHeight)

      gc.fill = Color.White
      maze.passages.foreach { passage =>
        val x = passage.col * scaleFactor
        val y = passage.row * scaleFactor
        gc.fillRect(x, y, scaleFactor, scaleFactor)
      }

      gc.fill = Color.Blue // Rat color
      val ratX = rat.currentPos.col * scaleFactor
      val ratY = rat.currentPos.row * scaleFactor
      gc.fillRect(ratX, ratY, scaleFactor, scaleFactor)

      gc.fill = Color.Green
      val endX = mazeWid * scaleFactor - scaleFactor
      val endY = length * scaleFactor - scaleFactor
      gc.fillRect(endX, endY, scaleFactor, scaleFactor)

      gc.setStroke(Color.Red)
      gc.setLineWidth(3)

      maze.bridges.foreach { bridge =>
        val entrance1 = bridge.entrance1
        val entrance2 = bridge.entrance2
        val x1 = entrance1.col * scaleFactor + scaleFactor / 2
        val y1 = entrance1.row * scaleFactor + scaleFactor / 2
        val x2 = entrance2.col * scaleFactor + scaleFactor / 2
        val y2 = entrance2.row * scaleFactor + scaleFactor / 2

        if (highlightSolution && (solution.contains(entrance1)) && (solution.contains(entrance2))) {
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
          val x = passage.col * scaleFactor + scaleFactor / 2
          val y = passage.row * scaleFactor + scaleFactor / 2
          gc.strokeOval(x, y, scaleFactor / 4, scaleFactor / 4)
        }
      }

      gc.setStroke(Color.DarkMagenta)
      gc.setLineWidth(1)
      gc.strokeText(s"Moves: ${movesTaken}", scaleFactor / 2, canvasHeight - scaleFactor / 2)
    }

    drawMaze()

    canvas.requestFocus()
    canvas.focusTraversable = true

    canvas.onKeyPressed = (event: KeyEvent) => {
      event.code match {
        case KeyCode.Up =>
          rat.moveUp(maze)
          movesTaken += 1
        case KeyCode.Down =>
          rat.moveDown(maze)
          movesTaken += 1
        case KeyCode.Left =>
          rat.moveLeft(maze)
          movesTaken += 1
        case KeyCode.Right =>
          rat.moveRight(maze)
          movesTaken += 1
        case KeyCode.Space =>
          rat.moveToOtherEnd(maze)
          movesTaken += 1
        case KeyCode.H =>
          highlightSolution = true
          helpUsed = true
          solveAndHighlight()
        case _ =>
      }

      if (rat.currentPos == Passage(maze.len - 1, maze.wid - 1)) {
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

    val mainScene = new Scene(root, canvasWidth, canvasHeight, Color.rgb(20, 20, 20))

    stage = new JFXApp3.PrimaryStage {
      title = "lost in maze"
      width = canvasWidth + scaleFactor
      height = canvasHeight + scaleFactor * 5
      scene = mainScene
    }
  }
}
