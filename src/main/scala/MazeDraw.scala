import MazeGUI.{canvasHeight, canvasWidth, gc, highlightSolution, length, maze, mazeWid, movesTaken, opponentRat, panOffsetX, panOffsetY, rat, scaleFactor, solution}
import scalafx.scene.paint.Color

object MazeDraw {
    def drawMaze(): Unit = {
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
          val x = (passage.col + panOffsetX) * scaleFactor + scaleFactor / 2 + offsetX
          val y = (passage.row + panOffsetY) * scaleFactor + scaleFactor / 2 + offsetY
          gc.strokeOval(x, y, scaleFactor / 4, scaleFactor / 4)
        }
      }

      gc.setStroke(Color.OrangeRed)
      gc.setLineWidth(1)
      gc.strokeText(s"Moves: ${movesTaken}", scaleFactor / 2, canvasHeight - scaleFactor / 2)
    }
}
