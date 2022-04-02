import scala.annotation.tailrec
import scala.collection.mutable
import scala.io.StdIn.readLine
import scala.collection.mutable.Stack

/**
 * The main menu. This is the first thing that the user sees, and is the hub for the application.
 */
object MenuManager extends Manager {

  /**
   * Greets the user and offers options.
   */
  override def greet(): Unit = {
    println("Welcome to WWEasy 0.0.1\n")
    println("Select [1] to view WWE stock data")
    println("Select [2] to view WWE PPV data")
    println("Select [0] to exit")
  }

  /**
   * Handles the user-input and offers them a more specific Manager based on what they choose
   * @return another Manager for the user
   */
  def handleInput(): Manager = {
    val valids = List(1, 2, 0)
    val input = super.getInput(valids)
    StateTracker.inputTracker.push(input)
    input match {
      case 1 => new StockDataManager
      case 2 => new PPVDataManager
      case 0 =>
        System.out.println("Goodbye")
        sys.exit(0)
      case _ => throw new IllegalArgumentException("Error: tried to match invalid input")
    }
  }
}