import scala.math.Ordered

abstract class DataWithDate(val date: String) extends Ordered[DataWithDate] {

  override def compare(that: DataWithDate): Int = this.date.compare(that.date)
}
