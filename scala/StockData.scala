/**
 * A class that defines StockData.
 * This data is found in Yahoo! Finance in csv form.
 *
 * @param date the date in which the stock price was measured
 * @param open the opening price of the stock during that day
 * @param high the highest price of the stock during that day
 * @param low the lowest price of the stock during that day
 * @param close the closing price of the stock during that day
 * @param adjClose the closing price of that stock adjusted for dividends
 * @param volume the number of shares sold during that day
 */
class StockData(override val date: String, val open: Float,
                val high: Float, val low: Float, val close: Float,
                val adjClose: Float, val volume: Int) extends DataWithDate(date) with Ordered[DataWithDate] {


  /**
   * Compares StockData objects by date in ascending order.
   *
   * @param that the second DataWithDate object to be compared to
   *  @return A negative integer if this < that, 0 if this == that, a positive integer otherwise
   */
  override def compare(that: DataWithDate): Int = super.compare(that)


  /**
   * Formats a StockData object as a String on a single line
   *
   * @return StockData as a String
   */
  override def toString: String = {
    var result = date
    result += "   " + open
    result += "   " + high
    result += "   " + low
    result += "   " + close
    result += "   " + adjClose
    result += "   " + volume
    result
  }
}
