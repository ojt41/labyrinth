// This is a standalone object used for displaying instructions using the GUI
object Instructions {
  val ins: String =
    """Lost in Maze - Instructions

      |Overview:
      |"Lost in Maze" is a maze-solving game where you, the player, control a rat trying to find its way out of a maze.
      |The maze is filled with passages, bridges, and obstacles. Your goal is to reach the exit while avoiding obstacles
      |and possibly being eliminated by the Opponent rat.

      |Starting the Game:
      |When you start the game, you will be prompted with a dialog to choose between loading a saved game or starting a new one.
      |  - Load Game: If you have a saved game, you can load it and continue from where you left off.
      |  - New Game: Start a new game by entering the dimensions of the maze (length and width).

      |Controls:
      |- Use the arrow keys (Up, Down, Left, Right) to move the rat through the maze.
      |- Spacebar: Move the rat to the other end of a bridge, if available.
      |- H: Highlight the solution path to the exit (Useful hint).
      |- R: Reset the zoom level to default.


      |Gameplay:
      |- Your rat starts in the center of the maze.
      |- Navigate through passages and bridges to reach the exit.
      |- The maze contains bridges that connect different parts of the maze, use space key to use a bridge.
      |- The opponent rat will also be moving in the maze, hoping to eliminate you.
      |- If your rat and the opponent rat collide, you will be eliminated.
      |- You can use the H key to give up (highlighted solution path).
      |- The number of moves you take is displayed at the bottom left corner.

      |Winning and Losing:
      |- Winning: Reach the exit without being eliminated and without using hints.
      |- Losing:
      |   - If you collide with the opponent rat, you are eliminated.
      |   - If you use hints, you can still finish the maze, but you wont get a highscore.

      |Victory Message:
      |- When you win, a victory message will display.
      |- If you set a new highscore, you can enter your username.
      |- You can choose to save the maze for future reference. Saved mazes can be played again.

      |Zoom and Pan:
      |- Use the mouse scroll wheel to zoom in or out of the maze.
      |- Click and drag the mouse to move around the maze.

      |Have Fun!
      |- Explore the maze, strategize your moves, and try to labyinth.
      |- See if you can solve the maze without using hints.

      |Enjoy playing "Lost in Maze"!
    """.stripMargin

}
