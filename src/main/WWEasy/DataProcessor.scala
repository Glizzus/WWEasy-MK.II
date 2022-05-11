import scala.annotation.tailrec
import scala.collection.immutable.TreeMap
import java.time.LocalDate

/** Provides methods for introducing and manipulating data.
 *
 * @author Cal Crosby
 */
case object DataProcessor {



  /** A method that converts a csv of valid data types into a DateMap.
   *
   * The valid data types currently include StockData and PpvData.
   * @param fileName the name of the .csv file to be read in
   * @return a DateMap with each date as a key to the respective data
   */
  def csvToDateMap(fileName: String): DateMap = {
    if (!isValidFile(fileName)) throw new IllegalArgumentException("File does not exist or is invalid")
    val buffered = scala.io.Source.fromFile(fileName)
    val lines = buffered.getLines()

    @tailrec
    def tailRecParser(map: TreeMap[LocalDate, Any], lines: Iterator[String]): TreeMap[LocalDate, Any] = {
      if !lines.hasNext then map
      else {
        val line = lines.next().split(',')
        val date = LocalDate.parse(line(0))
        inferType(line) match {
          case Some(typeToParse) =>
            val functionToParseWith = matchStringToParser(typeToParse)
            tailRecParser(map + (date -> functionToParseWith(line)), lines)
          case None =>
            println("Invalid line: " + line.mkString(", "))
            tailRecParser(map, lines)
        }
      }
    }
    lines.next() // This skips the CSV header
    val csvMap = tailRecParser(TreeMap[LocalDate, Any](), lines)
    buffered.close
    DateMap(csvMap)
  }


  def isValidFile(fileName: String): Boolean = {
    fileName.length() > 4 && fileName.takeRight(4) == ".csv" &&
    java.io.File(fileName).exists()
  }

  // TODO: Make this more reliable; maybe regex
  def inferType(csvLine: Array[String]): Option[String] = {
    csvLine.length match {
      case 2 => Option("p")
      case 4 => Option("r")
      case 7 => Option("s")
      case _ => None
    }
  }

  val parsePpvData: Array[String] => PpvData = line => PpvData(line(1))

  val parseStockData: Array[String] => StockData = line => {
    StockData(line(1).toFloat, line(2).toFloat, line(3).toFloat,
      line(4).toFloat, line(5).toFloat, line(6).toInt)
  }

  val parseRatingsData: Array[String] => RatingsData = line => {
    RatingsData(line(1),
      line(2).toFloatOption.getOrElse(-1.toFloat),
      line(3).toIntOption.getOrElse(-1))
  }

  def matchStringToParser(dataType: String): Array[String] => PpvData | StockData | RatingsData = {
    dataType.replaceFirst("-", "") match {
      case "p" | "ppv" => parsePpvData
      case "s" | "stock" => parseStockData
      case "r" | "ratings" => parseRatingsData
      case _ => throw new IllegalArgumentException()
    }
  }
  
  def dateMapToCsv(fileName: String, map: DateMap): Unit = {
    import java.io.{BufferedWriter, FileWriter, File}
    val writer = BufferedWriter(FileWriter(File(fileName)))
    writer.write("type,date,data\n")

    @tailrec
    def recursiveWriter(map: DateMap): Unit = {
      if map.isEmpty then println("Export finished")
      else {
        val minKey = map.data.firstKey
        val date = minKey.toString
        val dataCsvString = map.data.getOrElse(minKey, "") match {
          case r: RatingsData => r.toCsvString
          case s: StockData => s.toCsvString
          case p: PpvData => p.toCsvString
        }
        writer.write(s"$date,$dataCsvString")
        if map.size != 1 then writer.write("\n") // Ensures there is no newline at the end of the file
        recursiveWriter(map - minKey)
      }
    }
    recursiveWriter(map)
    writer.close()
  }
}