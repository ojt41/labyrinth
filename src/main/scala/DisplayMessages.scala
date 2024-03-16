import MazeGUI.{askForUserName, eliminated, game, helpUsed, length, maze, mazeWid, movesTaken, opponentRat, rat, showSaveMaze, spamKKey, stage, start}
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.stage.FileChooser

object DisplayMessages {

  def errorthrow() =
    val newAlert = Alert(AlertType.Error)
    newAlert.title = "Invalid input"
    newAlert.contentText = "Enter a valid positive integer between 10 and 200."
    newAlert.showAndWait()
    start()

  def lengthObtainer(result :Option[String]) =
    try{
          length = result.getOrElse("").toInt
          if length > 200 || length < 10 then
            throw Error()
        }
        catch
          case _ =>
            errorthrow()

  def widthObtainer(result :Option[String]) =
    try{
          mazeWid = result.getOrElse("").toInt
          if mazeWid > 200 || mazeWid < 10 then
            throw Error()
        }
        catch
          case _ =>
            errorthrow()

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
}
