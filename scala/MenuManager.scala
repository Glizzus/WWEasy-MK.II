import scala.annotation.tailrec
import scala.collection.mutable
import scala.io.StdIn.readLine
import scala.collection.mutable.Stack

object MenuManager {

  val inputTracker = new mutable.Stack[Int]


  def mainGreeting(): Unit = {
    println("Welcome to WWEasy 0.0.1\n")
    println("Select [1] to view WWE stock data")
    println("Select [2] to view WWE PPV data")
    println("Select [0] to exit")
  }

  /**
   * Gets the user input.
   * This is technically tail-recursive, but it shouldn't really come into play unless the user
   * makes a lot of incorrect inputs.
   *
   * @return the input of the user
   */
  @tailrec
  def getInput: Int = {
    val input = readLine.toIntOption.getOrElse(-1) // -1 flags that the input was not an Int
    input match {
      case -1 =>
        println("Invalid input: try again")
        getInput
      case _ =>
        if isValidInput(input) then input // ensures that the number is in the list of valid inputs
        else {
          println("Invalid input: try again")
          getInput
        }
    }
  }

  /**
   * Checks if the input is in the valid list of inputs
   * @param input the input to be checked
   * @return true if the input is valid, false otherwise
   */
  private def isValidInput(input: Int): Boolean = {
    val valids = List(1, 2, 0)
    valids.contains(input)
  }

  def matchInput(input: Int): Manager[Date] = input match {
    case 1 =>
      inputTracker.push(input) // TODO: These are side effects, refactor to be more functional
      StockDataManager
    case 2 =>
      inputTracker.push(input)
      PPVDataManager
    case 0 => sys.exit(0)
    case _ => throw new IllegalArgumentException("Error: tried to match invalid input")
  }
}