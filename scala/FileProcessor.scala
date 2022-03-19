import scala.io.Source

/**
 * Contains methods for processing different types of files
 * into different types of objects.
 */
object FileProcessor {

  
  /**
   * Generates a list of StockData from a CSV file.
   *
   * @param fileName the csv to be read
   * @return the LinkedList with all of the lines processed into objects
   */
  def stockDataFromCSV(fileName: String): LinkedList[DataWithDate] = {
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

  
  /**
   * Generates a list of PPVData from a CSV file.
   *
   * @param fileName the csv to be read
   * @return the LinkedList with all of the lines processed into objects
   */
  def PPVDataFromCSV(fileName: String): LinkedList[DataWithDate] = {
    val source = Source.fromFile(fileName)
    val list = new LinkedList[DataWithDate](null, null, 0)
    var header = true
    for (line <- source.getLines) {
      if (header) {
        header = false    // TODO: Find a better way to deal with CSV Headers
      }
      else {
        val temp = line.split(',')
        val data = new PPVData(temp(0), temp(1))
        list.add(data)
      }
    }
    source.close()
    list
  }
}
