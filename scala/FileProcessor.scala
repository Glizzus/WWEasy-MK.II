import scala.io.Source

/**
 * Contains methods for processing different types of files
 * into different types of objects.
 *
 * @author Cal Crosby
 */
object FileProcessor {

  // TODO: Fix how this code works with headers
  /**
   * Generates a list of StockData from a CSV file.
   *
   * @param csv the csv to be read
   * @param header whether the CSV has a header or not
   * @return the LinkedList with all of the lines processed into objects
   */
  def stockDataFromCSV(csv: String, header: Boolean = true): LinkedList[Date] = {
    if !isCSV(csv) then throw new IllegalArgumentException("File is not a csv file")

    val source = Source.fromFile(csv)
    val list = new LinkedList[Date]

    var headerSwitch = true  // TODO: This is some of the worst code I have ever written.
    for (line <- source.getLines) {
      if (header && headerSwitch) {
        headerSwitch = false
      }
      else {
        val temp = line.split(',')
        val dateTemp = dateArray(temp(0))

        val data = new StockData(dateTemp(0), dateTemp(1), dateTemp(2),
                                 temp(1).toFloat, temp(2).toFloat, temp(3).toFloat,
                                  temp(4).toFloat, temp(5).toFloat, temp(6).toInt)
        list += data
      }
    }
    source.close()
    list
  }


  /**
   * Generates a list of PPVData from a CSV file.
   *
   * @param csv the csv to be read
   * @return the LinkedList with all of the lines processed into objects
   */
  def PPVDataFromCSV(csv: String, header: Boolean = true): LinkedList[Date] = {
    if !isCSV(csv) then throw new IllegalArgumentException("File is not a csv file")

    val source = Source.fromFile(csv)
    val list = new LinkedList[Date]

    var headerSwitch = true  // TODO: This is some of the worst code I have ever written.
    for (line <- source.getLines) {
      if (header && headerSwitch) {
        headerSwitch = false
      }
      else {
        val temp = line.split(',')
        val dateTemp = dateArray(temp(0))

        val data = new PPVData(dateTemp(0), dateTemp(1), dateTemp(2), temp(1))
        list += data
      }
    }
    source.close()
    list
  }


  /**
   * This method extracts the header from a csv file.
   *
   * @param fileName the fileName to get the csv header from
   * @return
   */
  def getCSVHeader(fileName: String): String = {
    val source = Source.fromFile(fileName)
    val header = source.getLines.next
    source.close()
    header
  }


  // TODO: rename this function
  def makeHeaderDataTuple(csv: String,
                          headerFunc: String => String,
                          dataFunc: (String, Boolean) => LinkedList[Date]): (String, LinkedList[Date]) = {
    (headerFunc(csv), dataFunc(csv, true))
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


  def isCSV(fileName: String): Boolean = fileName.takeRight(4) == ".csv"

}
