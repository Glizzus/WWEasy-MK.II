/**
 * A class that defines StockData.
 * This data is found in Yahoo! Finance in csv form.
 *
 * @author Cal Crosby
 *
 * @param year the year of the date in which the data was taken
 * @param month the month of the year in which the data was taken
 * @param day the day of the year in which the data was taken
 *
 * @param open the opening price of the stock during that day
 * @param high the highest price of the stock during that day
 * @param low the lowest price of the stock during that day
 * @param close the closing price of the stock during that day
 * @param adjClose the closing price of that stock adjusted for dividends
 * @param volume the number of shares sold during that day
 */
class StockData(val year: Int, val month: Int, val day: Int, // TODO: Consolidate parameters
                val open: Float, val high: Float, val low: Float, val close: Float,
                val adjClose: Float, val volume: Int) extends Date {


  /**
   * Checks if an object is eligible to be checked for equality.
   *
   * @param obj any object
   * @return true if checking is allowed, false otherwise
   */
  def canEqual(obj: Any): Boolean = obj.isInstanceOf[StockData]


  /**
   * Checks if two StockData objects are equal. Unlike compare(), which
   * only checks the date, this method checks every field
   *
   * @param that the StockData object to be compared
   * @return true if equal, false otherwise
   */
  override def equals(that: Any): Boolean = that match {
    case that: StockData => {
      that.canEqual(this) &&
        this.compare(that) == 0 && //this checks if the dates are equal
        this.open == that.open &&
        this.high == that.high &&
        this.low == that.low &&
        this.close == that.close &&
        this.adjClose == that.adjClose &&
        this.volume == that.volume
    }
    case _ => false
  }


  /**
   * Compares StockData objects by date in ascending order.
   * Unlike equals(), which checks every field, this only compares dates
   *
   * @param that the second DataWithDate object to be compared to
   * @return A negative integer if this < that, 0 if this == that, a positive integer otherwise
   */
  override def compare(that: Date): Int = super.compare(that)


  /**
   * Formats a StockData object as a String on a single line
   *
   * @return StockData as a String
   */
  override def toString: String = {
    f"${super.toString}   $open%2.3f   $high%2.3f   $low%2.3f   $close%2.3f   $adjClose%2.3f   $volume"
  }
}
