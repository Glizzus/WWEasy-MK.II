import scala.io.Source

//TODO: Add more exception handling

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
   * This runs in worse-case O(N**2) time, but with sorted data (such as most csv files),
   * it runs in O(N) time.
   *
   * @param csv the csv to be read
   * @param header whether the CSV has a header or not
   * @return the LinkedList with all of the lines processed into objects
   */
  def stockDataFromCSV(csv: String, header: Boolean = true): List[Date] = {
    if !isCSV(csv) then throw new IllegalArgumentException("File is not a csv file")

    val source = Source.fromFile(csv)
    val listBuilder = new ListBuilder[Date]

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
        listBuilder += data
      }
    }
    source.close()
    listBuilder.toList
  }


  /**
   * Generates a list of PPVData from a CSV file.
   * This runs in worse-case O(N**2) time, but with sorted data (such as most csv files),
   * it runs in O(N) time.
   *
   * @param csv the csv to be read
   * @return the LinkedList with all of the lines processed into objects
   */
  def PPVDataFromCSV(csv: String, header: Boolean = true): List[Date] = {
    if !isCSV(csv) then throw new IllegalArgumentException("File is not a csv file")

    val source = Source.fromFile(csv)
    val listBuilder = new ListBuilder[Date]

    var headerSwitch = true  // TODO: This is some of the worst code I have ever written.
    for (line <- source.getLines) {
      if (header && headerSwitch) {
        headerSwitch = false
      }
      else {
        val temp = line.split(',')
        val dateTemp = dateArray(temp(0))

        val data = new PPVData(dateTemp(0), dateTemp(1), dateTemp(2), temp(1))
        listBuilder += data
      }
    }
    source.close()
    listBuilder.toList
  }


  /**
   * This method extracts the header from a csv file.
   *
   * @param fileName the fileName to get the csv header from
   * @return the header of the csv file
   */
  def getCSVHeader(fileName: String): String = {
    val source = Source.fromFile(fileName)
    val header = source.getLines.next
    source.close()
    header
  }


  // TODO: rename this function

  /**
   * This function returns a tuple containing a .csv header and its data as a LinkedList
   *
   * @param csv the file to be read
   * @param csvFunc the function to operate on the file. The function must take a .csv file and return a boolean
   * @return a tuple of the form (header, LinkedList[Date])
   */
  def makeHeaderDataTuple(csv: String, csvFunc: (String, Boolean) => List[Date]):
                           (String, List[Date]) = (getCSVHeader(csv), csvFunc(csv, true))


  /**
   * A utility method for turning a date string into
   * an Array of ints
   *
   * @param str the input date formatted as year-month-date
   * @return the Array(year, month, date) with each member as an int
   */
  private def dateArray(str: String): Array[Int] = {
    val temp = str.split('-')
    Array(temp(0).toInt, temp(1).toInt, temp(2).toInt)
  }

  /**
   * Determines if a given file is a .csv file
   * 
   * @param fileName the file to be checked
   * @return true if the file is a csv, false otherwise
   */
  private def isCSV(fileName: String): Boolean = fileName.takeRight(4) == ".csv"

}
