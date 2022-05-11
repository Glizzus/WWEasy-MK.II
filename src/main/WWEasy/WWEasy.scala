import scala.annotation.tailrec

/** The front-facing application for the client.
 *
 * This tail-recursively serves a loop for the client, where the user can load .csv files into
 * dataframes. Dataframes are tables of key-value pairs, with the key being a date and the value being anything.
 * The user can manipulate dataframes that are loaded in by filtering them or merging them with other dataframes.
 * Afterwards, a user can export a dataframe to a .csv file for later use.
 *
 * This makes heavy use of the State class, which represents the state of the program and holds all previous
 * dataframes so that the program can run recursively.
 * */
object WWEasy extends App {

  println("Welcome to WWEasy")
  mainLoop(State())

  @tailrec
  def mainLoop(state: State): Unit = {
    val newState = inputMatcher(state, getInput)
    mainLoop(newState)
  }

  def getInput: String = {
    print("\nWWEasy: ")
    scala.io.StdIn.readLine()
  }

  /** Matches user input to it's respective command and returns the new State.
   *
   * Some commands create a new State (e.g. load), while others simply access the old state without making a new one
   * (e.g. print). This catches any exceptions and returns the old state, allowing the program to continue
   * if the user makes an unexpected error.
   *
   * Ideally, the program should end at the quit command shown below.
   * @param oldState the last state of the program. This includes all of the dataframes that the user has manipulated.
   */
  def inputMatcher(oldState: State, line: String): State = {

    try {
      val input = line.split(' ')
      val command = input(0)

      command match {

        //-------------------------------- Creating a new State -----------------------------//

        case "load" =>
          val newState = oldState.addLoadedDataFrame(input)
          newState

        case "filter" =>
          val newState = oldState.addFilteredDataFrame(input)
          newState

        case "merge" =>
          val newState = oldState.addMergedDataFrame(input)
          newState

        case "clear" =>
          val newState = oldState.clearSpecificDataFrame(input)
          newState

        case "clearall" =>
          val newState = State()
          newState

        //-------------------------------- Reusing the Old State -----------------------------//

        case "filedump" =>
          fileDump()
          oldState

        case "renamefile" =>
          renameFile(input)
          oldState

        case "ids" =>
          oldState.printAllIds()
          oldState

        case "getcsv" =>
          downloadCsv(input)
          oldState

        case "export" =>
          oldState.exportMapToCsv(input)
          oldState

        case "print" =>
          oldState.printMap(input)
          oldState

        case "help" =>
          help(input)
          oldState

        case "quit" => // This is where the recursion ends (ideally)
          if userConfirmsQuit() then {
            println("Goodbye")
            sys.exit(0)
          }
          else oldState

        case _ =>
          println("Invalid input: please try again")
          println("Refer to [help] for more information")
          oldState
      }
    }
    catch {
      case exception: Exception =>
        exception match {
          case _: ArrayIndexOutOfBoundsException =>
            println("Invalid number of arguments entered; refer to [help] to see proper usage of commands")
          case exception: Exception => println("Error: " + exception)
        }
        oldState
    }
  }

  private def fileDump(): Unit = new java.io.File("src/Resources").list().foreach(println)

  private def renameFile(input: Array[String]): Unit = {
    if input.length == 3 then {
      val oldName = input(1)
      val newName = input(2)
      val renameAttempt = new java.io.File(oldName).renameTo(new java.io.File(newName))
      if renameAttempt then println(s"$oldName successfully renamed to $newName") else println("Rename failed")
    }
    else {
      println("Invalid number of arguments entered")
      printHelp("renamefile")
    }
  }

  private def downloadCsv(input: Array[String]): Unit = {

    def grabWweStockWithArgs(start: String, end: String, interval: String, directory: String): Unit = {
      StockDataCsvGrabber.main(Array[String]("WWE", start, end, interval, directory))
    }

    def grabWweStockNoArgs(): Unit = StockDataCsvGrabber.main(Array[String]("WWE"))

    val typeToScrape = input(1)
    typeToScrape match {
      case "-r" | "-ratings" =>
        Utils.spinOffThread(WWERatingsCsvGrabber.grabRatings()) // This takes like 20 minutes so we spin off a thread
      case "-s" | "-stock" =>
        input.length match {
          case 2 => grabWweStockNoArgs()
          case 6 => grabWweStockWithArgs(input(2), input(3), input(4), input(5))
          case _ =>
            println("Invalid number of arguments entered")
            printHelp("getcsv")
        }
      case fail =>
        println(s"Invalid data type entered \"$fail\"")
        printHelp("getcsv")
    }
  }
  
  private def userConfirmsQuit(): Boolean = {
    println("Are you sure you wish to quit? No data will be saved")
    println("Export any data that you plan on keeping using the [export] command")
    scala.io.StdIn.readLine("Type \"quit\" to confirm: ").equalsIgnoreCase("quit")
  }

  private def help(input: Array[String]): Unit = {
    if input.length == 1 then printHelp()
    else printHelp(input(1))
  }

  @tailrec
  def printHelp(command: String = "all"): Unit = {
    command match {

      case "all" =>
        println("\nProvides an interface for manipulating WWE related data into dataframes")
        println("load [file] [new id]                            print [id]")
        println("filter [-criteria] [id] [args] [new id]         getcsv [-datatype] [args]")
        println("merge [id] [id] [new id]                        help [command]")
        println("quit                                            clear [id]")
        println("clearall                                        filedump")
        println("renamefile [oldfile] [newfile]                  ids")
        println("export [id] [filename]")

      case "load" =>
        println("\nLoads a .csv file into the program as a dataframe\n")
        println("Currently, one can load WWE ratings data, Yahoo! Finance historical stock data, and WWE PPV data\n")

        println("USAGE: load [file] [new id]")
        println("EXAMPLE: load some_data.csv MyData")

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
        
        println("Also, you can use wildcards \"_\" in order to only filter by year, month, or date")
        println("EXAMPLE 1: filter -d MyStockData < ____-05-__ DataBeforeMay")
        println("EXAMPLE 2: filter -d MyStockData < ____-__-01 NewYearsData\n")


        println("For filtering by Show, valid arguments include \"raw, \"smackdown, \"nxt")
        println("EXAMPLE: filter -s MyShowsData raw MyRawData")

      case "getcsv" =>
        println("\nGets a .csv file from the internet\n")

        println("USAGE: getcsv [-datatype] [args]")
        println("Valid datatypes include Ratings (-r) and Stock (-s)\n")

        println("To get a Ratings .csv, pass in no arguments")
        println("EXAMPLE: getcsv -r\n")

        println("To get a Stock .csv, you can pass in nothing, or pass in arguments here.\n")
        println("Arguments include [start_date] and [end_date] (yyyy-mm-dd), an [interval] " +
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

      case "filedump" =>
        println("\nShows each file in the Resources file.\n")
        println("USAGE: filedump")

      case "renamefile" =>
        println("\nRenames a file\n")
        println("USAGE: renamefile [oldfile] [newfile]")
        println("EXAMPLE: renamefile resources/oldname resources/newname")

      case "ids" =>
        println("\nShows all ID's for dataframes\n")
        println("USAGE: ids")

      case "export" =>
        println("\nExports a dataframe to a .csv file\n")
        println("USAGE: export [id] [filename]")
        println("EXAMPLE: export MyData resources/data.csv")

      case _ => printHelp()
    }
  }
}