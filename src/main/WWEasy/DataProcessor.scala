import scala.annotation.tailrec
import scala.collection.immutable.TreeMap
import java.time.LocalDate

/** Provides methods for introducing and manipulating data.
 *
 * @author Cal Crosby
 */
case object DataProcessor {

  val validTypes: Seq[String] = List("-p", "-ppv", "-s", "-stock", "-r", "-ratings")


  /** A method that converts a csv of valid data types into a DateMap.
   *
   * The valid data types currently include StockData and PpvData.
   * @param fileName the name of the .csv file to be read in
   * @param dataType a char which represents the type of the data to be parsed
   * @return a DateMap with each date as a key to the respective data
   */
  def csvToDateMap(fileName: String, dataType: String): DateMap = {
    if (!isValidType(dataType)) throw new IllegalArgumentException("Invalid type of data requested")
    if (!isValidFile(fileName)) throw new IllegalArgumentException("File does not exist or is invalid")
    val buffered = scala.io.Source.fromFile(fileName)
    val lines = buffered.getLines()

    @tailrec
    def tailRecParser(map: TreeMap[LocalDate, Any], lines: Iterator[String]): TreeMap[LocalDate, Any] = {
      if !lines.hasNext then map
      else {
        val line = lines.next().split(',')
        val date = LocalDate.parse(line(0))
        dataType.toLowerCase().replaceFirst("-", "") match {
          case "p" | "ppv" =>
            tailRecParser(map + (date -> parsePpvData(line)), lines)
          case "s" | "stock" => parseStockData(line)
            tailRecParser(map + (date -> parseStockData(line)), lines)
          case "r" | "ratings" =>
            tailRecParser(map + (date -> parseRatingsData(line)), lines)
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


  def isValidType(dataType: String): Boolean = {
    validTypes.contains(dataType.toLowerCase)
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


  def dateMapToCsv(fileName: String, map: DateMap): Unit = {
    import java.io.File
    import java.io.BufferedWriter
    import java.io.FileWriter
    val writer = BufferedWriter(FileWriter(File(fileName)))
    writer.write("type,date,data\n")

    @tailrec
    def recursiveWriter(map: DateMap): Unit = {
      if map.isEmpty then println("Export finished")
      else {
        val minKey = map.data.firstKey
        val dateToWrite = minKey.toString
        writer.write(map.data.getOrElse(minKey, "") match {
          case r: RatingsData => r.toCsvString(dateToWrite)
          case s: StockData => s.toCsvString(dateToWrite)
          case p: PpvData => p.toCsvString(dateToWrite)
          case "" => ""
        })
        writer.write("\n")
        recursiveWriter(map - minKey)
      }
    }
    recursiveWriter(map)
    writer.close()
  }






}