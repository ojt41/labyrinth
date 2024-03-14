object Instructions {
  val ins: String =
    """Lost in Maze - Instructions

      |Overview:
      |"Lost in Maze" is a maze-solving game where you the player controls a rat trying to find its way out of a maze.
      |The maze is filled with passages, bridges, and obstacles. Your goal is to reach the exit while avoiding obstacles
      |and possibly being eliminated by the Opponent rat.

      |Starting the Game:
      |1. When you start the game, you will be prompted with a dialog to choose between loading a saved game or starting a new one.
      |   - **Load Game**: If you have a saved game, you can load it and continue from where you left off.
      |   - **New Game**: Start a new game by entering the dimensions of the maze (length and width).

      |Controls:
      |- Use the arrow keys (**Up**, **Down**, **Left**, **Right**) to move the rat through the maze.
      |- **Spacebar**: Move the rat to the other end of a bridge, if available.
      |- **H**: Highlight the solution path to the exit (Useful hint).
      |- **R**: Reset the zoom level to default.
      |- **Any other key**: If the rat encounters an opponent or reaches the exit, any key will start a new game.

      |Gameplay:
      |- Your rat starts at the entrance of the maze.
      |- Navigate through passages and bridges to reach the exit (marked in green).
      |- Avoid obstacles and dead ends.
      |- The maze may contain bridges that connect different parts of the maze.
      |- The opponent rat will also be moving towards its own goal.
      |- If your rat and the opponent rat collide, you will be eliminated.
      |- You can use the **H** key to see a hint (highlighted solution path).
      |- The number of moves you take is displayed at the bottom left corner.

      |Winning and Losing:
      |- **Winning**: Reach the exit before the opponent rat, without being eliminated.
      |- **Losing**:
      |  - If you collide with the opponent rat, you are eliminated.
      |  - If you reach the exit after the opponent rat, you lose.

      |Victory Message:
      |- When you win, a victory message will display.
      |- If you set a new highscore, you can enter your username.
      |- You can choose to save the maze for future reference.

      |Zoom and Pan:
      |- Use the mouse scroll wheel to zoom in or out of the maze.
      |- Click and drag the mouse to pan around the maze.

      |Have Fun!
      |- Explore the maze, strategize your moves, and try to beat the opponent rat.
      |- See if you can solve the maze without using too many hints.

      |Enjoy playing "Lost in Maze"!
    """.stripMargin
}