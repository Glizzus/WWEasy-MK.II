import scala.annotation.tailrec
import scala.io.StdIn.readLine

/**
 * A class for managing PPV Data.
 */
class PPVDataManager extends Manager {

  /**
   * Greets the user and offers them options.
   */
  override def greet(): Unit = {
    println("Welcome to the WWEasy Pay-Per-View Analyzer")
    println("Select [1] to enter a .csv file")
    println("Select [2] to view the current data")
    println("Select [3] to manipulate the current data")
    println("Select [9] to go back")
    println("Select [0] to exit")
  }

  /**
   * Handles the user input.
   * This usually recursively calls itself for more user input.
   * Any modifications made to the state of the data is handled in StateTracker
   * This is final so that it can be tail-recursive and save stack frames.
   *
   * @return another Manager for the user
   */
  @tailrec
  final def handleInput(): Manager = {
    val valids = List(1, 2, 3, 9, 0)
    val input = getInput(valids)
    StateTracker.inputTracker.push(input)
    input match  {
      case 1 =>
        val list = enterCSV()
        StateTracker.ppvDataHistory.push(list)
        println(".csv successfully processed")
        greet()
        handleInput()
      case 2 =>
        println(StateTracker.ppvDataHistory.top)
        handleInput()
      case 3 => throw new UnsupportedOperationException()
      case 9 => throw new UnsupportedOperationException()
      case 0 =>
        println("Goodbye")
        sys.exit(0)
      case _ => throw new IllegalArgumentException("Error: tried to match invalid input")
    }
  }

  /**
   * A utility method for entering .csv files
   * @return a List of PPVData
   */
  private def enterCSV(): List[Date] = {
    val file = readLine("Enter the .csv file: ")
    FileProcessor.PPVDataFromCSV(file, true)
  }
}
