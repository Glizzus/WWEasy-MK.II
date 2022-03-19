import scala.io.Source

object FileProcessor {

  def StockDataFromCSV(fileName: String): LinkedList[DataWithDate] = {
    val source = Source.fromFile(fileName)
    val list = new LinkedList[DataWithDate](null, null, 0)
    var header = true
    for (line <- source.getLines) {
      if (header) {
        header = false   // TODO: Find a better way to deal with CSV Headers
      }
      else {
        val temp = line.split(',')
        val data = new StockData(temp(0), temp(1).toFloat, temp(2).toFloat,
                                 temp(3).toFloat, temp(4).toFloat, temp(5).toFloat,
                                  temp(6).toInt)
        list.add(data)
      }
    }
    source.close()
    list
  }

}
