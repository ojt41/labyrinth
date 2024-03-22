import MazeDraw.drawMaze
import scalafx.Includes.*
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.canvas.{Canvas, GraphicsContext}
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.ButtonType.Close
import scalafx.scene.control.{Alert, TextArea, TextInputDialog}
import scalafx.scene.input.{KeyCode, KeyEvent, MouseEvent, ScrollEvent}
import scalafx.scene.layout.BorderPane
import scalafx.scene.paint.Color
import scalafx.scene.text.{Font, FontWeight}

import java.awt.Robot
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random


object MazeGUI extends JFXApp3 {
  val robot = new Robot()
  // Declare variables for maze dimensions and game elements.
  var length: Int = _
  var mazeWid: Int = _
  val game = new Game(new Rat(Passage(length / 2, mazeWid / 2)), new Storage)
  var maze: Maze = _
  var rat: Rat = _
  var opponentRat: Rat = _

  // Variables for game state
  var highlightSolution: Boolean = false
  var solution: Array[Passage] = Array.empty
  var helpUsed = false
  var eliminated = false
  var movesTaken = 0

  // Variables for canvas dimensions, graphics context, and panning
  var scaleFactor: Double = 1.0
  var canvasWidth: Double = _
  var canvasHeight: Double = _
  var canvas: Canvas = _
  var gc: GraphicsContext = _
  var lastMouseX: Double = _
  var lastMouseY: Double = _
  var panOffsetX: Double = 0.0
  var panOffsetY: Double = 0.0

  def showInstructions(): Unit = {
    val instructionsTextArea = new TextArea {
      text = Instructions.ins
      editable = false
      wrapText = true
      prefWidth = 800
      prefHeight = 600
      font = scalafx.scene.text.Font.font("Calibri", FontWeight.SemiBold, 14)
    }
    val instructionsDialog = new Alert(AlertType.Information) {
      title = "Instructions"
      headerText = "Maze Game Instructions"
      dialogPane().content = instructionsTextArea
    }
    val inputs = instructionsDialog.showAndWait()
    inputs match
      case _ =>
        start()
  }

  def solveAndHighlight(): Unit = solution = maze.solveMaze(rat)

  // Method to prompt user for filename and save the maze
  def showSaveMaze(maze: Maze): Unit = {
    val saveDialog = new TextInputDialog() {
      title = "Save Maze"
      headerText = "Enter a filename if you want to save this maze:"
    }
    val saveResult = saveDialog.showAndWait()
    saveResult match {
      case Some("") =>
      case Some(filename) =>
        game.storage.writeMazeData(maze, filename)
        println(s"Maze saved to: $filename")
      case None =>
    }
  }

  def askForUserName(maze: Maze): Unit = {
    val giveName = new TextInputDialog() {
      title = "Username"
      headerText = "Enter a username for this highscore"
    }
    val saveResult = giveName.showAndWait()
    saveResult match {
      case Some(str: String) =>
        maze.highscore = (str, movesTaken)
      case _ =>
    }
  }

  // Toggles CapsLock key to exit current thread and activate main thread
  def spamKKey(): Future[Unit] = Future {
    {
      robot.keyPress(java.awt.event.KeyEvent.VK_CAPS_LOCK)
      robot.keyRelease(java.awt.event.KeyEvent.VK_CAPS_LOCK)
      robot.keyPress(java.awt.event.KeyEvent.VK_CAPS_LOCK)
      robot.keyRelease(java.awt.event.KeyEvent.VK_CAPS_LOCK)
    }
  }

  override def start(): Unit = {
    eliminated = false
    helpUsed = false
    movesTaken = 0

    val loadOrNewDialog = new Alert(AlertType.Confirmation) {
      title = "Main Menu"
      headerText = "Do you want to load a game or start a new one?"
      contentText = "Choose your option:"
      buttonTypes =
        Seq(new javafx.scene.control.ButtonType("New Game"), new javafx.scene.control.ButtonType("Load Game"),
          new javafx.scene.control.ButtonType("Instructions"), javafx.scene.control.ButtonType.CLOSE)
    }

    val result = loadOrNewDialog.showAndWait()
    result match {
      case Some(loadGameButton) if loadGameButton.getText == "Load Game" => {
        DisplayMessages.loadMaze()
      }
      case Some(instructions) if instructions.getText == "Instructions" => {
        showInstructions()
      }
      case Some(Close) if Close.getText == "Close" =>
        System.exit(0)

      case _ => {
        val dialogLength = new TextInputDialog() {
          title = "Enter Length"
          headerText = "Enter a valid integer between 10 and 200 as the length of maze:"
        }

        val dialogWidth = new TextInputDialog() {
          title = "Enter Width"
          headerText = "Enter a valid integer between 10 and 200 as the width of the maze:"
        }

        val lengthResult = dialogLength.showAndWait()
        val widthResult = dialogWidth.showAndWait()
        DisplayMessages.dimensionObtainer(lengthResult, widthResult)

        maze = game.newMaze(length, mazeWid)
        rat = game.rat
        game.startGame(maze)
      }
    }
    spamKKey()

    val passageInRight = maze.passages.filter(n => {
      n.x == 1
    }).last //this places enemy in right top coner
    opponentRat = Rat(passageInRight)

    val maxLength = math.max(mazeWid, length)
    scaleFactor = 800 / maxLength
    canvasWidth = 800
    canvasHeight = 800
    canvas = new Canvas(canvasWidth, canvasHeight)
    gc = canvas.graphicsContext2D

    MazeDraw.drawMaze()

    // reset zoom with "R"
    def resetZoom(): Unit = {
      scaleFactor = 800 / maxLength
      panOffsetX = 0.0
      panOffsetY = 0.0
    }

    canvas.requestFocus()
    canvas.focusTraversable = true

    // handle keypresses
    canvas.onKeyPressed = (event: scalafx.scene.input.KeyEvent) => {
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
        case KeyCode.R =>
          resetZoom()
          drawMaze()
        case _ =>
          if eliminated then
            DisplayMessages.showVictoryMessage()
            start()
      }

      if (rat.currentPos == Passage(maze.len - 1, maze.wid - 1) || eliminated) {
        DisplayMessages.showVictoryMessage()
        start()
      }
      if (rat.currentPos == opponentRat.currentPos) {
        eliminated = true
        DisplayMessages.showVictoryMessage()
        start()
      }
      drawMaze()
    }

    canvas.onKeyReleased = (event: scalafx.scene.input.KeyEvent) => {
      event.code match {
        case KeyCode.H =>
          highlightSolution = false
          drawMaze()
        case _ =>
      }
    }

    var lastOpponentRatMove: Passage = opponentRat.currentPos

    def moveOpponentRat(): Unit =
      val possibleMoves = maze.possiblePassages(opponentRat.currentPos)
      val validMoves = possibleMoves.filter(maze.validPassage)
        .filter(_ != lastOpponentRatMove) // Exclude last move

      if (validMoves.nonEmpty) then
        val newOpponentRatPos = validMoves(Random.nextInt(validMoves.length))
        lastOpponentRatMove = opponentRat.currentPos
        opponentRat.currentPos = newOpponentRatPos

      if (possibleMoves.length == 1) then
        val helper = Passage(opponentRat.currentPos.x, opponentRat.currentPos.y) // this makes a true copy of the currentPos
        opponentRat.currentPos = lastOpponentRatMove
        lastOpponentRatMove = helper


    val opponentRatMoveTask = new Runnable {
      def run(): Unit =
        moveOpponentRat()
        drawMaze()
    }

    val opponentRatMoveTimer = Future {
      val time = 350 // this is time in ms
      while (rat.currentPos != opponentRat.currentPos && rat.currentPos != Passage(maze.len - 1, maze.wid - 1)) {
        opponentRatMoveTask.run()
        if (rat.currentPos == opponentRat.currentPos) then
          eliminated = true
          DisplayMessages.showVictoryMessage()
          MazeGUI.start()

        Thread.sleep(time) // delay of val time milliseconds, value set at 350 ms so 3 moves a second.
      }
      eliminated = true
      DisplayMessages.showVictoryMessage()
      game.endGame(maze, opponentRat)
    }

    // handle zooming and panning
    canvas.onScroll = (event: ScrollEvent) => {
      val zoomFactor = 1.1
      val deltaY = event.deltaY
      if (deltaY < 0) then scaleFactor /= zoomFactor //Zoom out 
      else scaleFactor *= zoomFactor //Zoom in
      drawMaze()
      event.consume()
    }

    canvas.onMousePressed = (event: MouseEvent) => {
      lastMouseX = event.x
      lastMouseY = event.y
    }

    canvas.onMouseDragged = (event: MouseEvent) => {
      val deltaX = event.x - lastMouseX
      val deltaY = event.y - lastMouseY
      lastMouseX = event.x
      lastMouseY = event.y
      panOffsetX += deltaX / scaleFactor
      panOffsetY += deltaY / scaleFactor
      drawMaze()
    }

    canvas.onMouseReleased = (event: MouseEvent) => {
      lastMouseX = event.x
      lastMouseY = event.y
    }

    val root = new BorderPane
    root.center = canvas


    val mainScene = new Scene(root, canvasWidth, canvasHeight, Color.rgb(20, 20, 20))

    stage = new JFXApp3.PrimaryStage {
      title = "lost in maze"
      width = canvasWidth max 800
      height = canvasHeight max 800
      scene = mainScene
    }
  }
}
