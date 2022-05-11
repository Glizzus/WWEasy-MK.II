import scala.annotation.targetName
import scala.collection.immutable.HashMap

/** Encapsulates the State of the program so that it can run recursively.
 *  
 * @author Cal Crosby
 * @param maps Each DateMap created by the user, stored in a HashMap for fast random access
 */
case class State(maps: HashMap[String, DateMap] = HashMap[String, DateMap]()) {


  def addLoadedDataFrame(input: Array[String]): State = {
    val csvFilePath = input(1)
    input.length match {

      case 3 =>
        val newName = input(2)
        val map = DataProcessor.csvToDateMap(csvFilePath)
        println(s".csv successfully loaded into $newName")
        State(maps + (newName -> map))
        
      case _ =>
        println("Invalid number of arguments entered")
        WWEasy.printHelp("load")
        this
    }
  }


  def addFilteredDataFrame(input: Array[String]): State = {
    if input.length == (5 | 6) then {
      val whatToFilterBy = input(1)
      val dataToFilter = input(2)
      maps.get(dataToFilter) match {
        case Some(map) =>
          whatToFilterBy match {
            case "-d" | "-date" => filterByDate(input, map)
            case "-s" | "-show" => filterByShow(input, map)
            case fail =>
              println(s"Invalid argument \"$fail\"")
              WWEasy.printHelp("filter")
              this
          }
        case None =>
          println(s"Queried data \"$dataToFilter\" not found")
          this
      }
    }
    else {
      println("Invalid number of arguments entered")
      WWEasy.printHelp("filter")
      this
    }
  }

  private def filterByDate(input: Array[String], map: DateMap): State = {
    if input.length == 6 then {
      val operator = input(3)
      val date = input(4)
      val newName = input(5)
      val filtered = map.filterByDate(date, operator)
      println(s"Filter successfully applied; data loaded into $newName")
      State(maps + (newName -> filtered))
    }
    else {
      println("Invalid number of arguments entered")
      WWEasy.printHelp("filter")
      this
    }
  }

  private def filterByShow(input: Array[String], map: DateMap): State = {
    if input.length == 5 then {
      val show = input(3)
      if isValidShow(show) then {
        val newName = input(4)
        val filtered = map.filter((_, data) => filterIndividualDataByShow(data, show))
        println(s"Filter successfully applied; data loaded into $newName")
        State(maps + (newName -> filtered))
      }
      else {
        println(s"Invalid show \"$show\"")
        WWEasy.printHelp("filter")
        this
      }
    }
    else {
      println("Invalid number of arguments")
      WWEasy.printHelp("filter")
      this
    }
  }

  private def isValidShow(show: String): Boolean = {
    val validShows = List("raw", "smackdown", "nxt")
    validShows.contains(show)
  }

  private def filterIndividualDataByShow(data: Any, show: String): Boolean = {
    data match {
      case r: RatingsData =>
        show match {
          case "raw" => r.isRaw
          case "smackdown" => r.isSmackDown
          case "nxt" => r.isNxt
          case _ => throw new IllegalArgumentException("This should be unreachable; validate the show input")
        }
      case _ => true
    }
  }


  def addMergedDataFrame(input: Array[String]): State = {

    def tryMerge(mapStr1: String, mapStr2: String): Option[DateMap] = {
      val getMap1 = maps.get(mapStr1)
      getMap1 match {
        case Some(map1) =>
          val getMap2 = maps.get(mapStr2)
          getMap2 match {
            case Some(map2) =>
              Some(map1.merge(map2))
            case None => None
          }
        case None => None
      }
    }

    val mapStr1 = input(1)
    val mapStr2 = input(2)
    val merged = tryMerge(mapStr1, mapStr2)
    merged match {
      case Some(map) =>
        val newName = input(3)
        println(s"Maps successfully merged into $newName")
        State(maps + (newName -> map))
      case None =>
        println(s"Queried Data does not exist")
        this
    }
  }


  def clearSpecificDataFrame(input: Array[String]): State = {
    val dataToClear = input(1)
    if maps.contains(dataToClear) then {
      println(s"Removed $dataToClear")
      State(maps - dataToClear)
    }
    else {
      println(s"Unable to find $dataToClear")
      this
    }
  }


  def printAllIds(): Unit = maps.foreach((id, _) => println(id))


  def exportMapToCsv(input: Array[String]): Unit = {
    val idToExport = input(1)
    val fileName = addCsvExtension(input(2))
    val possibleMap = maps.get(idToExport)
    possibleMap match {
      case Some(map) =>
        DataProcessor.dateMapToCsv(fileName, map)
      case None =>
        println("Queried data does not exist")
    }
  }

  private def addCsvExtension(fileName: String): String = {
    s"$fileName${if fileName.takeRight(4) != ".csv" then ".csv" else ""}"
  }


  def printMap(input: Array[String]): Unit = {
    val dataToPrint = maps.get(input(1))
    dataToPrint match {
      case Some(data) =>
        println(s"\n[${input(1)}]:\n$data")
      case None =>
        println("Queried data not present")
    }
  }
}

  

