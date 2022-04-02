import FileProcessor._
import MenuManager._

object Main extends App {

  mainGreeting()
  val input = getInput
  val manager = matchInput(input)

  manager.greet()
  









}