import scala.annotation.tailrec
import scala.io.StdIn.readLine

/**
 * A trait for managing menus for the user
 */
trait Manager {

  
  /**
   * This method simply greets the users and offers them the menu.
   */
  def greet(): Unit

  /**
   * This method handles input from the user. This is usually implemented recursively with
   * getInput
   * @return another Manager for the user
   */
  def handleInput(): Manager

  /**
   * This method gets user inputs and determines if it is valid base on the parameter.
   * This is tail-recursive, which saves stack frames if the user uses the application for a while.
   * 
   * @param valids a list of integers that are valid inputs
   * @return the input of the user if it is valid
   */
  @tailrec
  final def getInput(valids: List[Int]): Int = {
    val input = readLine.toIntOption.getOrElse(-1) // -1 flags that the input was not an Int
    if isValidInput(input, valids) then input
    else {
      println("Invalid input: try again")
      getInput(valids)
    }
  }

  /**
   * A utility method to determine if an input is valid.
   * 
   * @param input the input the check
   * @param valids the list of valid inputs
   * @return true if the input is valid, false otherwise
   */
  private def isValidInput(input: Int, valids: List[Int]): Boolean = {
    valids.contains(input)
  }
}
