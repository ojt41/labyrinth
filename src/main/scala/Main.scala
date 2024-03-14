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
import scalafx.stage.FileChooser

import java.awt.Robot
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random


object MazeGUI extends JFXApp3 {
  val robot = new Robot()

  var length: Int = _
  var mazeWid: Int = _

  val game = new Game(new Rat(Passage(length / 2, mazeWid / 2)), new Storage)
  var maze: Maze = _
  var rat: Rat = _
  var opponentRat: Rat = _

  var highlightSolution: Boolean = false
  var solution: Array[Passage] = Array.empty

  var helpUsed = false
  var eliminated = false
  var movesTaken = 0

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
        font = scalafx.scene.text.Font.font("Calibri", FontWeight.Normal, 14)
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

  def solveAndHighlight(): Unit = {
    solution = maze.solveMaze(rat)
  }

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

  def loadMaze(): Unit = {
    val fileChooser = new FileChooser()
    fileChooser.title = "Load Maze"
    val selectedFile = fileChooser.showOpenDialog(stage)
    if (selectedFile != null) {
      try{
      maze = game.storage.readMazeData(selectedFile.getPath)
      }
      catch
        case e: Exception =>
         val newallert = new Alert(AlertType.Error)
         newallert.title = "Wrong format"
         newallert.contentText = "Load a valid Maze file"
         newallert.showAndWait()
         loadMaze()
     finally
          length = maze.len
          mazeWid = maze.wid
          rat = game.rat
          game.startGame(maze)
    }
  }

  def showVictoryMessage(): Unit = {
    spamKKey()
    println("show victory message")
    if eliminated then println("it has been eliminated")
    val alert = new Alert(AlertType.Information)
    println(eliminated)
    alert.title = if eliminated then
      "You lost"
    else if (!helpUsed && !game.endGame(maze, opponentRat)) then
      "Congratulations!"
    else
      "Game Over"

    alert.headerText =
      if (!helpUsed && !eliminated) then
        "Victory!"
      else if (eliminated) then
        "You lost"
      else
        "Try to solve the maze on your own."

    alert.contentText =
      if (!helpUsed && !eliminated && maze.highscore._2 > movesTaken) then
        askForUserName(maze)
        (s"You have reached the exit in ${movesTaken} moves. You have beaten the highscore ${maze.highscore._1}.")
      else if (!helpUsed && !eliminated) then
        (s"You have reached the exit in ${movesTaken} moves. Congratulations on solving the maze!")

      else if (eliminated) then
        "You were eleminated by the opponent"
      else
        "Good job! Try to solve the maze next time without using the hints."
    alert.showAndWait()
    showSaveMaze(maze)
  }


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
      buttonTypes = Seq( new javafx.scene.control.ButtonType("New Game"),new javafx.scene.control.ButtonType("Load Game"),
        new javafx.scene.control.ButtonType("Instructions"),
        javafx.scene.control.ButtonType.CLOSE)
    }


    val result = loadOrNewDialog.showAndWait()
    result match {
      case Some(loadGameButton) if loadGameButton.getText == "Load Game" =>{
        loadMaze()
      }
      case Some(instructions) if instructions.getText == "Instructions" => {
        showInstructions()
      }
      case Some(Close) if Close.getText == "Close" =>
        System.exit(0)

      case _ => {
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
        }
    }

    spamKKey()


    //maze.passages.foreach(x => println(x.toString()))
    val passageInRight = maze.passages.filter(n=> {n.x == 1}).last  //this places enemy in right top coner
    opponentRat = Rat(passageInRight)

    val maxLength = math.max(mazeWid, length)
    scaleFactor = 800 / maxLength
    canvasWidth = 800 //scaleFactor * mazeWid
    canvasHeight = 800 //scaleFactor * length

    canvas = new Canvas(canvasWidth, canvasHeight)
    gc = canvas.graphicsContext2D

    def drawMaze(): Unit = {
      // Calculate the centering offsets
      val offsetX = (canvasWidth - mazeWid * scaleFactor) / 2
      val offsetY = (canvasHeight - length * scaleFactor) / 2

      gc.fill = Color.Black
      gc.fillRect(0, 0, canvasWidth, canvasHeight)

      gc.fill = Color.White
      maze.passages.foreach { passage =>
        val x = (passage.col + panOffsetX) * scaleFactor + offsetX
        val y = (passage.row + panOffsetY) * scaleFactor + offsetY
        gc.fillRect(x, y, scaleFactor, scaleFactor)
      }

      gc.fill = Color.Blue // Rat color
      val ratX = (rat.currentPos.col + panOffsetX) * scaleFactor + offsetX
      val ratY = (rat.currentPos.row + panOffsetY) * scaleFactor + offsetY
      gc.fillRect(ratX, ratY, scaleFactor, scaleFactor)

      gc.fill = Color.Green
      val endX = (mazeWid + panOffsetX) * scaleFactor - scaleFactor + offsetX
      val endY = (length + panOffsetY) * scaleFactor - scaleFactor + offsetY
      gc.fillRect(endX, endY, scaleFactor, scaleFactor)

      gc.fill = Color.Red // enenmy
      val opponentRatX = (opponentRat.currentPos.col + panOffsetX) * scaleFactor + offsetX
      val opponentRatY = (opponentRat.currentPos.row + panOffsetY) * scaleFactor + offsetY
      gc.fillRect(opponentRatX, opponentRatY, scaleFactor, scaleFactor)

      val bridgeColor = Color.Blue
      gc.setStroke(bridgeColor)
      gc.setLineWidth(3)

      maze.bridges.foreach { bridge =>
        val entrance1 = bridge.entrance1
        val entrance2 = bridge.entrance2
        val x1 = (entrance1.col + panOffsetX) * scaleFactor + scaleFactor / 2 + offsetX
        val y1 = (entrance1.row + panOffsetY) * scaleFactor + scaleFactor / 2 + offsetY
        val x2 = (entrance2.col + panOffsetX) * scaleFactor + scaleFactor / 2 + offsetX
        val y2 = (entrance2.row + panOffsetY) * scaleFactor + scaleFactor / 2 + offsetY

        if (highlightSolution && (solution.contains(entrance1)) && (solution.contains(entrance2))) {
          gc.setStroke(Color.Green)
        } else {
          gc.setStroke(bridgeColor)
        }

        gc.strokeLine(x1, y1, x2, y2)
      }

      if (highlightSolution) {
        gc.setStroke(Color.Green)
        gc.setLineWidth(2)
        solution.foreach { passage =>
          val x = (passage.col + panOffsetX) * scaleFactor + scaleFactor / 2 + offsetX
          val y = (passage.row + panOffsetY) * scaleFactor + scaleFactor / 2 + offsetY
          gc.strokeOval(x, y, scaleFactor / 4, scaleFactor / 4)
        }
      }

      gc.setStroke(Color.OrangeRed)
      gc.setLineWidth(1)
      gc.strokeText(s"Moves: ${movesTaken}", scaleFactor / 2, canvasHeight - scaleFactor / 2)
    
      if (highlightSolution) {
        gc.setStroke(Color.Green)
        gc.setLineWidth(2)
        solution.foreach { passage =>
          val x = (passage.col + panOffsetX) * scaleFactor + scaleFactor / 2
          val y = (passage.row + panOffsetY) * scaleFactor + scaleFactor / 2
          gc.strokeOval(x, y, scaleFactor / 4, scaleFactor / 4)
        }
      }

      gc.setStroke(Color.OrangeRed)
      gc.setLineWidth(1)
      gc.strokeText(s"Moves: ${movesTaken}", scaleFactor / 2, canvasHeight - scaleFactor / 2)
    }




    drawMaze()


    def resetZoom(): Unit = {
      scaleFactor = 800 / maxLength
      panOffsetX = 0.0
      panOffsetY = 0.0
    }

    canvas.requestFocus()
    canvas.focusTraversable = true

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
            showVictoryMessage()
            start()
      }

      if (rat.currentPos == Passage(maze.len - 1, maze.wid - 1) || eliminated) {
        showVictoryMessage()
        start()
      }
      if (rat.currentPos == opponentRat.currentPos) {
        eliminated = true
        println("condition met")
        showVictoryMessage()
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

    def moveOpponentRat(): Unit = {
      val possibleMoves = maze.possiblePassages(opponentRat.currentPos)
      val validMoves = possibleMoves.filter(maze.validPassage)
        .filter(_ != lastOpponentRatMove) // Exclude last move

      if (validMoves.nonEmpty) {
        val newOpponentRatPos = validMoves(Random.nextInt(validMoves.length))
        lastOpponentRatMove = opponentRat.currentPos
        opponentRat.currentPos = newOpponentRatPos
      }
      if (possibleMoves.length == 1) {
        val helper = Passage(opponentRat.currentPos.x, opponentRat.currentPos.y) // this makes a true copy of the currentPos
        opponentRat.currentPos = lastOpponentRatMove
        lastOpponentRatMove = helper
      }
    }


    val opponentRatMoveTask = new Runnable {
      def run(): Unit =
        moveOpponentRat()
        drawMaze()
    }

    val opponentRatMoveTimer = Future {
      val time = 350   // this is time in ms
      while (rat.currentPos != opponentRat.currentPos && rat.currentPos != Passage(maze.len - 1, maze.wid - 1)) {
        opponentRatMoveTask.run()
        if (rat.currentPos == opponentRat.currentPos) {
          eliminated = true
          println("within while loop")
          showVictoryMessage()
          MazeGUI.start()
      }
        Thread.sleep(time) // delay of val time milliseconds, value set at 200 ms so 5 moves a second.
      }
      eliminated = true
      println("hello")
      println(rat.currentPos)
      println(opponentRat.currentPos)
      showVictoryMessage()
      game.endGame(maze, opponentRat)
    }



    canvas.onScroll = (event: ScrollEvent) => {
      val zoomFactor = 1.1
      val deltaY = event.deltaY

      if (deltaY < 0) {
        // Zoom out
        scaleFactor /= zoomFactor
      } else {
        // Zoom in
        scaleFactor *= zoomFactor
      }

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
    //root.setCenter(canvas)


    val mainScene = new Scene(root, canvasWidth, canvasHeight, Color.rgb(20, 20, 20))

    stage = new JFXApp3.PrimaryStage {
      title = "lost in maze"
      width = canvasWidth max 800
      height = canvasHeight max 800
      scene = mainScene
    }

    //stage.centerOnScreen()
  }
}
