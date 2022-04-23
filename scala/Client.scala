import scala.annotation.tailrec

object Client extends App {

  greet()
  inputLoop(State())


  @tailrec
  def inputLoop(state: State): Unit = {

    val input = scala.io.StdIn.readLine().split(' ')
    val command = input(0)
    command match {

      case "help" =>
        printHelp()
        inputLoop(state)

      case "load" =>

        val file = input(1)
        val dataType = input(2)
        val map = FileProcessor.csvToDateMap(file, dataType)
        println(s".csv successfully loaded into ${state.maps.length}")
        inputLoop(State(state.maps :+ map))

      case "filter" =>

        val indexOfMap = input(1).toInt
        val mapToFilter = state.maps(indexOfMap)
        val operator = input(2).charAt(0)
        val date = input(3)
        val filtered = mapToFilter.filterByDate(date, operator)
        println("filter successfully applied")
        inputLoop(State(state.maps :+ filtered))

      case "clear" =>
        inputLoop(State(Vector[DateMap]()))

      case "quit" =>
        println("Goodbye")
        sys.exit(0)

      case "merge" =>
        val map1 = state.maps(input(1).toInt)
        val map2 = state.maps(input(2).toInt)
        val merged = map1.merge(map2)
        inputLoop(State(state.maps :+ merged))

      case "show" =>
        if input(1) == "all" then dataDump(state.maps, 0)
        else {
          val indexOfMap = input(1).toInt
          println(state.maps(indexOfMap))
        }
        inputLoop(state)

      case _ =>
        println("Invalid input: please try again")
        inputLoop(state)
    }
  }

    def printHelp(): Unit =  {

        println("\nload [filename] [datatype]")
        println("\tLoads a .csv file into a DateMap. Valid datatypes include ppv and StockData.")

        println("filter [ID] [operator] [date]")
        println("\tCreates new DateMap by filtering a previous DateMap. Date should be in yyyy-mm-dd format.")

        println("clear")
        println("\tErases all previous DateMaps.")

        println("merge [ID] [ID]")
        println("\tCreates a new DateMap by merging two previous DateMaps. If there is a collision, the second DateMap" +
          "will take priority")

        println("show [ID] OR [all]")
        println("\tShows a DataMap based on its ID, or shows all Datamaps")
    }


    @tailrec
    def dataDump(seq: Seq[Any], index: Int): Unit = {
      if (index < seq.length) {
        print(s"[$index]: ")
        println(seq(index))
        dataDump(seq, index + 1)
      }
    }

    def greet(): Unit = println("Welcome to WWEasy")
  }

