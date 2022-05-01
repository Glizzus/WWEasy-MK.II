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
                        case "nxt" => r.isNxt
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
        val dataToClear = input(1)
        inputLoop(State(state.maps - dataToClear))

      case "clearall" =>
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

      case "print" =>
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
            spinOffThread(WWERatingsCsvGrabber.grabRatings())
            Thread.sleep(1000)
          case "-s" | "-stock" =>
            input.length match {
              case 2 => spinOffThread(grabWweStock())
              case 6 => spinOffThread(grabWweStock(input(2), input(3), input(4), input(5)))
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

  private def isValidShow(show: String): Boolean = {
    val validShows = List("raw", "smackdown", "nxt")
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


  def spinOffThread(block: => Unit): Unit = {
    new Thread {
      override def run(): Unit = block
    }.start()
  }


  @tailrec
  def printHelp(command: String = "all"): Unit = {
    command match {

      case "all" =>
        println("\nProvides an interface for manipulating WWE related data into dataframes")
        println("load [file] [-datatype] [new id]                print [id]")
        println("filter [-criteria] [id] [args] [new id]         getcsv [-datatype] [args]")
        println("merge [id] [id] [new id]                        help [command]")
        println("quit                                            clear [id]")
        println("clearall")

      case "load" =>
        println("\nLoads a .csv file into the program to manipulate\n")

        println("USAGE: load [file] [-datatypes] [new id]")
        println("Currently valid datatypes include Pay-Per-View (-p), Stock (-s), Ratings (-r)")
        println("EXAMPLE: load some_data.csv -p MyPPVData")

      case "print" =>
        println("\nPrints out a dataframe by its id\n")

        println("USAGE: print [id]")

      case "filter" =>
        println("\nFilters a dataframe by one of its attributes\n")

        println("USAGE: filter [-criteria] [id] [args] [new id]")
        println("Currently valid criteria include Date (-d), Show (-s)\n")

        println("For filtering by Date, valid arguments include a [relational operator] (<, =, >) followed by a " +
          "[date] in yyyy-mm-dd format")
        println("EXAMPLE: filter -d MyStockData < 2015-01-01 MyFilteredData\n")

        println("For filtering by Show, valid arguments include \"raw, \"smackdown, \"nxt")
        println("EXAMPLE: filter -s MyShowsData raw MyRawData")

      case "getcsv" =>
        println("\nGets a .csv file from the internet\n")

        println("USAGE: getcsv [-datatype] [args]")
        println("Valid datatypes include Ratings (-r) and Stock (-s)\n")

        println("To get a Ratings .csv, pass in no arguments")
        println("EXAMPLE: getcsv -r\n")

        println("To get a Stock .csv, you can pass in nothing, or pass in arguments here.\n" +
          "Arguments include [start_date] and [end_date] (yyyy-mm-dd), an [interval] " +
          "(daily, weekly, monthly), and a [directory].")
        println("EXAMPLE 1: getcsv -s")
        println("EXAMPLE 2: getcsv -s 2001-01-01 2005-01-01 weekly myDir/Resources")

      case "merge" =>
        println("\nMerges two dataframes into a single dataframe")
        println("\nBecause two entries can not share the same date, the second dataframe will take precedence\n")

        println("USAGE: merge [id] [id] [new id]")
        println("EXAMPLE: merge MyData1 myData2 MyMergedData")

      case "help" =>
        println("\nDisplays information about a command\n")
        println("USAGE: help [command]")
        println("EXAMPLE: help load")

      case "quit" =>
        println("\nExits the program. No dataframes are saved\n")
        println("USAGE: quit")

      case "clear" =>
        println("\nRemoves a dataframe.\n")
        println("USAGE: clear [id]")
        println("EXAMPLE: clear DataIDontNeed")

      case "clearall" =>
        println("\nRemoves all dataframes.\n")
        println("USAGE: clearall")

      case _ => printHelp()
    }
  }
}