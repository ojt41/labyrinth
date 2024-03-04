import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.canvas.{Canvas, GraphicsContext}
import scalafx.scene.control.Button
import scalafx.scene.layout.{BorderPane, HBox}
import scalafx.scene.paint.Color

object MazeGUI extends JFXApp3 {
  val game = Game(new Rat(Passage(0, 0)), new Timer, new Storage)
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

      gc.fill = Color.Blue
      val ratX = rat.currentPos.col * 20
      val ratY = rat.currentPos.row * 20
      gc.fillRect(ratX, ratY, 20, 20)

      gc.fill = Color.Green
      val endX = maze.wid * 20 - 20
      val endY = maze.len * 20 - 20
      gc.fillRect(endX, endY, 20, 20)
    }

    drawMaze()

    val moveUpButton = new Button("Up")
    moveUpButton.onAction = _ => {
      rat.moveUp()
      drawMaze()
    }

    val moveDownButton = new Button("Down")
    moveDownButton.onAction = _ => {
      rat.moveDown()
      drawMaze()
    }

    val moveLeftButton = new Button("Left")
    moveLeftButton.onAction = _ => {
      rat.moveLeft()
      drawMaze()
    }

    val moveRightButton = new Button("Right")
    moveRightButton.onAction = _ => {
      rat.moveRight()
      drawMaze()
    }

    val buttonBox = new HBox(10, moveUpButton, moveDownButton, moveLeftButton, moveRightButton)

    val root = new BorderPane
    root.center = canvas
    root.bottom = buttonBox

    val mainScene = new Scene(root, canvasWidth, canvasHeight, Color.rgb(20, 20, 20))

    stage = new JFXApp3.PrimaryStage {
      title = "Maze Game"
      width = canvasWidth + 20
      height = canvasHeight + 100
      scene = mainScene
    }
  }
}
