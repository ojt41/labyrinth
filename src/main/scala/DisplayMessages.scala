import MazeGUI.{length, mazeWid, start}
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType

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
}
