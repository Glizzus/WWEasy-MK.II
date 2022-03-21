import scala.io.Source

/**
 * Contains methods for processing different types of files
 * into different types of objects.
 *
 * @author Cal Crosby
 */
object FileProcessor {

  
  /**
   * Generates a list of StockData from a CSV file.
   *
   * @param fileName the csv to be read
   * @return the LinkedList with all of the lines processed into objects
   */
  def stockDataFromCSV(fileName: String): LinkedList[Date] = {
    val source = Source.fromFile(fileName)
    val list = new LinkedList[Date](null, null, 0)
    var header = true
    for (line <- source.getLines) {
      if (header) {
        header = false   // TODO: Find a better way to deal with CSV Headers
      }
      else {
        val temp = line.split(',')
        val dateTemp = dateArray(temp(0))

        val data = new StockData(dateTemp(0), dateTemp(1), dateTemp(2),
                                 temp(1).toFloat, temp(2).toFloat, temp(3).toFloat,
                                  temp(4).toFloat, temp(5).toFloat, temp(6).toInt)
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
  def PPVDataFromCSV(fileName: String): LinkedList[Date] = {
    val source = Source.fromFile(fileName)
    val list = new LinkedList[Date](null, null, 0)
    var header = true
    for (line <- source.getLines) {
      if (header) {
        header = false    // TODO: Find a better way to deal with CSV Headers
      }
      else {
        val temp = line.split(',')
        val dateTemp = dateArray(temp(0))

        val data = new PPVData(dateTemp(0), dateTemp(1), dateTemp(2), temp(1))
        list.add(data)
      }
    }
    source.close()
    list
  }


  /**
   * A utility method for turning a date string into
   * an Array of ints
   *
   * @param str the input date formatted as year-month-date
   * @return the Array(year, month, date) with each member as an int
   */
  def dateArray(str: String): Array[Int] = {
    val temp = str.split('-')
    Array(temp(0).toInt, temp(1).toInt, temp(2).toInt)
  }
}
