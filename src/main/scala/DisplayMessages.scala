import MazeGUI.{askForUserName, eliminated, game, helpUsed, length, maze, mazeWid, movesTaken, opponentRat, rat, showSaveMaze, spamKKey, stage, start}
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.stage.FileChooser

object DisplayMessages {

  def showErrorAlert() = // Gives warning if dimension input is invalid.
    val newAlert = Alert(AlertType.Error)
    newAlert.title = "Invalid input"
    newAlert.contentText = "Enter a valid positive integer between 10 and 200."
    newAlert.showAndWait()
    System.exit(0) // exits the game if error is caused.

  def dimensionObtainer(result1 :Option[String], result2: Option[String]) =
    try{
          length = result1.getOrElse(" ").toInt // throws an error if string cant be converted to Int.
          if length > 200 || length < 10 then // Restricts the dimensions to 10 to 200 (inclusive)
            throw Error()

          mazeWid = result2.getOrElse("").toInt // similar to lengthObtainer method.
          if mazeWid > 200 || mazeWid < 10 then
            throw Error()
        }
        catch
          case _ => // if error (invalid string to Int, or outside restricition, a warning is shown.)
            showErrorAlert()



  def showVictoryMessage(): Unit = {
    // handles the victory message depending on the circumstances of the gameplay.
    spamKKey()
    val alert = new Alert(AlertType.Information)
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
    // initialises a GUI based file selector. If file is corrupted/wrong format, a warning message is shown.
    val fileChooser = new FileChooser()
    fileChooser.title = "Load Maze"
    val selectedFile = fileChooser.showOpenDialog(stage)
    if (selectedFile != null) {
      try{
      maze = game.storage.readMazeData(selectedFile.getPath)
      }
      catch{
        case e: Exception =>
         val newallert = new Alert(AlertType.Error)
         newallert.title = "Wrong format"
         newallert.contentText = "Load a valid Maze file"
         newallert.showAndWait()
         loadMaze() // recursively call function loadMaze until valid file is selected.
      }

      finally                 //Game is initialised according to loaded file, if its correct format.
          length = maze.len
          mazeWid = maze.wid
          rat = game.rat
          game.startGame(maze)
    }
  }
}
