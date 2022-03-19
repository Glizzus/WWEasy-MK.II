import scala.io.Source
import scala.math.Ordered

class StockData(override val date: String, val open: Float,
                val high: Float, val low: Float, val close: Float,
                val adjClose: Float, val volume: Int) extends DataWithDate(date) with Ordered[DataWithDate] {


  override def toString: String = {
    var result = "||"
    result += date
    result += "   " + open
    result += "   " + high
    result += "   " + low
    result += "   " + close
    result += "   " + adjClose
    result += "   " + volume
    result += "||"
    result
  }

  override def compare(that: DataWithDate): Int = super.compare(that)
}
