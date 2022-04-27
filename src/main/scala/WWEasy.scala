import scala.annotation.tailrec

/** The front-facing application for the client.
 *
 * @author Cal Crosby
 */
object WWEasy extends App {

  greet()
  inputLoop(State())


  /** A tail-recursive loop that serves the application to the client.
   *
   * The user input is split and matched to
   * a functionality of the program. The ID of any given DateMap corresponds to its index in the state vector
   * @param state the current state of the program. This includes all of the DateMaps that the user has manipulated.
   */
  @tailrec
  def inputLoop(state: State): Unit = { // TODO: Add more exception handling, make this more Unix-like

    val input = scala.io.StdIn.readLine().split(' ')
    val command = input(0)
    command match {

      case "help" =>
        printHelp()
        inputLoop(state)

      case "load" =>

        val file = input(1)
        val dataType = input(2)
        val newName = input(3)
        val map = DataProcessor.csvToDateMap(file, dataType)
        println(s".csv successfully loaded into $newName") // This tells the user the ID of the DateMap
        inputLoop(State(state.maps + (newName -> map)))

      case "filter" =>

        val whatToFilterBy = input(1)
        val dataToFilter = input(2)

        whatToFilterBy match {

          case "-d" | "-date" =>
            val toFilter = state.maps.get(dataToFilter)
            toFilter match {
              case Some(map) =>
                val operator = input(3)
                val date = input(4)
                val newName = input(5)
                val filtered = map.filterByDate(date, operator)
                println(s"Filter successfully applied; data loaded into $newName")
                inputLoop(State(state.maps + (newName -> filtered)))
              case None =>
                println("Queried data does not exist: please try again")
                inputLoop(state)
            }

          case "-s" | "-show" =>
            val show = input(3)

            if (!isValidShow(show)) {
              println("Invalid show")
              inputLoop(state)
            }
            else {
              val toFilter = state.maps.get(dataToFilter)
              toFilter match {
                case Some(map) =>

                  val newName = input(4)
                  val filtered = map.filter({
                    case (_, r: RatingsData) =>
                      show match {
                        case "raw" => r.isRaw
                        case "smackdown" => r.isSmackDown
                      }
                    case _ => true
                  })
                  println(s"Filter successfully applied; data loaded into $newName")
                  inputLoop(State(state.maps + (newName -> filtered)))
                case None =>
                  println("Queried data does not exist: please try again")
                  inputLoop(state)
              }
            }
        }

      case "clear" =>
        inputLoop(State())

      case "quit" => // This is where the recursion ends (ideally)
        println("Goodbye")
        sys.exit(0)

      case "merge" =>
        val getMap1 = state.maps.get(input(1))
        getMap1 match {
          case Some(map1) =>
            val getMap2 = state.maps.get(input(2))
            getMap2 match {
              case Some(map2) =>
                val merged = map1.merge(map2)
                val newName = input(3)
                println(s"Maps successfully merged into $newName")
                inputLoop(State(state.maps + (newName -> merged)))
              case None =>
                println(s"The second data queried does not exist")
                inputLoop(state)
            }
          case None =>
            println("The first data queried does not exist")
            inputLoop(state)
        }

      case "show" =>
        if input(1) == "all" then dataDump(state.maps)
        else {
          val dataToPrint = state.maps.get(input(1))
          dataToPrint match {
            case Some(data) =>
              println(s"\n[${input(1)}]:\n$data")
            case None =>
              println("Queried data not present")
          }
        }
        inputLoop(state)

      case _ =>
        println("Invalid input: please try again")
        inputLoop(state)
    }
  }

  def printHelp(): Unit =  {
    println("load [file] [data type] [name]          help")
    println("")
  }


  private def isValidShow(show: String): Boolean = {
    val validShows = List("raw", "smackdown")
    validShows.contains(show)
  }

  /** This prints out each entry of a map along with its key.
   *
   * @param map any Map
   */
  private def dataDump(map: Map[String, Any]): Unit = {
    map.foreach((key, value) => println(s"\n[$key]:\n$value"))
  }

  private def greet(): Unit = println("Welcome to WWEasy")
}

