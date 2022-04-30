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
   *
   * @param state the current state of the program. This includes all of the DateMaps that the user has manipulated.
   */
  @tailrec
  def inputLoop(state: State): Unit = { // TODO: Add more exception handling, make this more Unix-like

    Thread.sleep(1000)
    print("\nWWEasy: ")
    val input = scala.io.StdIn.readLine().split(' ')
    val command = input(0)

    command match {

      case "help" =>
        if (isOneCommand(input)) printHelp()
        else printHelp(input(1))
        inputLoop(state)

      case "load" =>

        load(input(1), input(2))
        val newName = input(3)
        val map = load(input(1), input(2))
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
        val merged = tryMerge(input(1), input(2), state)
        merged match {
          case Some(map) =>
            val newName = input(3)
            println(s"Maps successfully merged into $newName")
            inputLoop(State(state.maps + (newName -> map)))
          case None =>
                println(s"Queried Data does not exist")
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

      case "getcsv" =>
        input(1) match {
          case "-r" | "-ratings" =>
            new Thread { override def run(): Unit = { WWERatingsCsvGrabber.grabRatings() }}.start()
          case "-s" | "-stock" =>
            input.length match {
              case 2 => grabWweStock()
              case 6 => grabWweStock(input(2), input(3), input(4), input(5))
              case _ =>
                println("Invalid number of arguments entered")
            }
          case _ =>
            println("Invalid data type entered")
        }
        inputLoop(state)

      case _ =>
        println("Invalid input: please try again")
        inputLoop(state)
    }
  }

  def printHelp(command: String = "all"): Unit = {
    command match {
      case "all" =>
        println("load [file] [-datatype] [new id]                show [id]")
        println("filter [-criteria] [id] [-args] [new id]        clear")
        println("merge [id] [id] [new id]                        help [command]")
        println("quit")
    }

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

  def load(file: String, dataType: String): DateMap = {
    DataProcessor.csvToDateMap(file, dataType)
  }

  def tryMerge(mapStr1: String, mapStr2: String, state: State): Option[DateMap] = {
    val getMap1 = state.maps.get(mapStr1)
    getMap1 match {
      case Some(map1) =>
        val getMap2 = state.maps.get(mapStr2)
        getMap2 match {
          case Some(map2) =>
            Some(map1.merge(map2))
          case None => None
        }
      case None => None
    }
  }

  def isOneCommand(strings: Array[String]): Boolean = strings.length == 1

  def grabWweStock(start: String, end: String, interval: String, directory: String): Unit = {
    StockDataCsvGrabber.main(Array[String]("WWE", start, end, interval, directory))
  }

  def grabWweStock(): Unit = {
    StockDataCsvGrabber.main(Array[String]("WWE"))
  }
}