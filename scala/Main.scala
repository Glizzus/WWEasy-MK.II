import MenuManager._

object Main extends App {

  MenuManager.greet()
  val input = getInput
  val manager = handleInput()

  manager.greet()
  manager.handleInput()









}