import scala.annotation.tailrec
import scala.collection.immutable.TreeMap

case object FileProcessor {

  def ppvCsvToMap(fileName: String): DateMap = {
    val buffered = scala.io.Source.fromFile(fileName)
    val lines = buffered.getLines()

    @tailrec
    def helper(map: TreeMap[Date, Ppv], lines: Iterator[String]): TreeMap[Date, Ppv] = {
      if !lines.hasNext then map
      else {
        val line = lines.next().split(',')
        val date = parseDate(line(0))
        val ppv = Ppv(line(1))
        val addedMap = map + (date -> ppv)
        helper(addedMap, lines)
      }
    }
    lines.next() // This skips the CSV header
    val csvMap = helper(TreeMap[Date, Ppv](), lines)
    buffered.close()
    DateMap(csvMap)
  }

  def stockCsvToMap(fileName: String): DateMap = {
    val buffered = scala.io.Source.fromFile(fileName)
    val lines = buffered.getLines()

    @tailrec
    def helper(map: TreeMap[Date, StockData], lines: Iterator[String]): TreeMap[Date, StockData] = {
      if !lines.hasNext then map
      else {
        val line = lines.next().split(',')
        val date = parseDate(line(0))                     // Open, High, Low, Close, Adj-Close, Volume
        val addedMap = map + (date -> StockData(line(1).toFloat, line(2).toFloat, line(3).toFloat,
                                              line(4).toFloat, line(5).toFloat, line(6).toInt))
        helper(addedMap, lines)
      }
    }
    lines.next() // This skips the CSV header
    val csvMap = helper(TreeMap[Date, StockData](), lines)
    buffered.close()
    DateMap(csvMap)
  }


  def parseDate(strDate: String): Date = {
    val splitDate = strDate.split('-')
    Date(splitDate(0).toInt, splitDate(1).toInt, splitDate(2).toInt)

  }
}