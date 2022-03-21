import scala.math.Ordered

/**
 * A trait for any object that includes a date.
 * 
 * @author Cal Crosby
 */
trait Date extends Ordered[Date]{
  val year: Int
  val month: Int
  val day: Int


  /**
   * 
   * @return
   */
  override def toString: String = f"$year-$month%02d-$day%02d"

  /**
   * Compares two objects with the Date trait by their respective date.
   * 
   * @param that the object being compared
   * @return -1 if this < that, 0 if this == that, 1 else
   */
  override def compare(that: Date): Int = this.year.compare(that.year).sign match {
    case -1 => -1
    case 1 => 1
    case 0 => this.month.compare(that.month).sign match { //TODO: This nested match hurts to look at
      case -1 => -1
      case 1 => 1
      case 0 => this.day.compare(that.day)
    }
  }
}
