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
   * Formats a date as a String in yyyy-mm-dd form
   * @return the date as a String
   */
  override def toString: String = f"$year-$month%02d-$day%02d"

  /**
   * Compares two objects with the Date trait by their respective date.
   * 
   * @param that the object being compared
   * @return -1 if this < that, 0 if this == that, 1 else
   */
  override def compare(that: Date): Int =  {
    val yearComp = this.year.compare(that.year)
    if yearComp != 0 then yearComp
    else {          
      val monthComp = this.month.compare(that.month)
      if monthComp != 0 then monthComp
      else {
        val dayComp = this.day.compare(that.day)
        if dayComp != 0 then dayComp
        else 0
      }
    }
  }
}
