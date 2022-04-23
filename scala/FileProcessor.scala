import scala.annotation.tailrec
import scala.collection.immutable.TreeMap

case object FileProcessor {

  /**
   * A method that converts a csv of valid data types into a DateMap. The valid data types currently include
   * StockData and Ppv.
   * @param fileName the name of the .csv file to be read in
   * @param dataType a char which represents the type of the data to be parsed
   * @return a DateMap with each date as a key to the respective data
   */
  def csvToDateMap(fileName: String, dataType: String): DateMap = {
    if (!isValidType(dataType)) throw new IllegalArgumentException("Invalid type of data requested")
    if (!isCsv(fileName)) throw new IllegalArgumentException("File is not .csv file")
    val buffered = scala.io.Source.fromFile(fileName)
    val lines = buffered.getLines()

    @tailrec
    def tailRecParser(map: TreeMap[Date, Any], lines: Iterator[String]): TreeMap[Date, Any] = {
      if !lines.hasNext then map
      else {
        val line = lines.next().split(',')
        val date = parseDate(line(0))
        dataType.toLowerCase() match {
          case "ppv" =>
            tailRecParser(map + (date -> parsePpv(line)), lines)
          case "stockdata" => parseStockData(line)
            tailRecParser(map + (date -> parseStockData(line)), lines)
        }
      }
    }
    lines.next() // This skips the CSV header
    val csvMap = tailRecParser(TreeMap[Date, Any](), lines)
    buffered.close
    DateMap(csvMap)
  }

  def isCsv(fileName: String): Boolean = {
    fileName.length() > 4 && fileName.takeRight(4) == ".csv"
  }

  def isValidType(dataType: String): Boolean = {
    val validTypes = List("ppv", "stockdata")
    validTypes.contains(dataType.toLowerCase)
  }

  def parseDate(strDate: String): Date = {
    val splitDate = strDate.split('-')
    Date(splitDate(0).toInt, splitDate(1).toInt, splitDate(2).toInt)
  }

  def parsePpv(line: Array[String]): Ppv = Ppv(line(1))

  def parseStockData(line: Array[String]): StockData = {
    StockData(line(1).toFloat, line(2).toFloat, line(3).toFloat,
              line(4).toFloat, line(5).toFloat, line(6).toInt)
  }
}