import scala.math.Ordered

/**
 * An abstract class solely created so that data with dates can be sorted relative to each other.
 * If I develop a smarter way to do this, this class will be removed.
 * @param date the date that the data contains
 */
abstract class DataWithDate(val date: String) extends Ordered[DataWithDate] {

  /**
   * Compares two DataWithDate objects in ascending order by date.
   * @param that the second DataWithDate object to be compared to
   * @return A negative integer if this < that, 0 if this == that, a positive integer otherwise
   */
  override def compare(that: DataWithDate): Int = this.date.compare(that.date)
}
