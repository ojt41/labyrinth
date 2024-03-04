import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.canvas.{Canvas, GraphicsContext}
import scalafx.scene.input.KeyEvent
import scalafx.scene.layout.BorderPane
import scalafx.scene.paint.Color
import scalafx.Includes._

object MazeGUI extends JFXApp3 {
  val game = new Game(new Rat(Passage(0, 0)), new Timer, new Storage)
  val maze = game.newMaze(20, 20)
  val rat = game.rat

  override def start(): Unit = {
    val BAR_HEIGHT = 60
    val DEATH_BOX_WIDTH = 12

    val canvasWidth = maze.wid * 20
    val canvasHeight = maze.len * 20

    val canvas = new Canvas(canvasWidth, canvasHeight)
    val gc: GraphicsContext = canvas.graphicsContext2D

    def drawMaze(): Unit = {
      gc.fill = Color.White
      gc.fillRect(0, 0, canvasWidth, canvasHeight)

      gc.fill = Color.Black
      maze.passages.foreach { passage =>
        val x = passage.col * 20
        val y = passage.row * 20
        gc.fillRect(x, y, 20, 20)
      }

      gc.fill = Color.Blue // Rat color
      val ratX = rat.currentPos.col * 20
      val ratY = rat.currentPos.row * 20
      gc.fillRect(ratX, ratY, 20, 20)

      gc.fill = Color.Green // End point color
      val endX = maze.wid * 20 - 20
      val endY = maze.len * 20 - 20
      gc.fillRect(endX, endY, 20, 20)
    }

    drawMaze()

    canvas.requestFocus()
    canvas.focusTraversable = true

    canvas.onKeyPressed = (event: KeyEvent) => {
      event.code match {
        case scalafx.scene.input.KeyCode.Up => rat.moveUp(maze.passages)
        case scalafx.scene.input.KeyCode.Down => rat.moveDown(maze.passages)
        case scalafx.scene.input.KeyCode.Left => rat.moveLeft(maze.passages)
        case scalafx.scene.input.KeyCode.Right => rat.moveRight(maze.passages)
        case _ =>
      }
      drawMaze()
    }

    val root = new BorderPane
    root.center = canvas

    val mainScene = new Scene(root, canvasWidth, canvasHeight, Color.rgb(20, 20, 20))

    stage = new JFXApp3.PrimaryStage {
      title = "Maze Game"
      width = canvasWidth + 20
      height = canvasHeight + 100
      scene = mainScene
    }
  }
}
