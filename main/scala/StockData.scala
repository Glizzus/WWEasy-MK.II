/** Encapsulates WWE stock data. This data is generally from Yahoo! Finance.
 * 
 * @author Cal Crosby
 * @param open the opening price of WWE stock on a given date
 * @param high the peak price of WWE stock on a given date
 * @param low the lowest price of WWE stock on a given date
 * @param close the closing price of WWE stock on a given date
 * @param adjClose the adjusted-closing price of WWE stock (i. e. factoring in dividends) on a given date
 * @param volume the amount of shares traded on a given date
 */
case class StockData(open: Float, high: Float, low: Float, close: Float,
                     adjClose: Float, volume: Int) {

  override def toString: String = {
    f"$open%2.3f   $high%2.3f   $low%2.3f   $close%2.3f   $adjClose%2.3f   $volume"
  }
}
