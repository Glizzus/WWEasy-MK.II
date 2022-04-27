/** Encapsulates information about dates.
 *
 * It has extensive checking
 * that only valid calendar dates are entered, even making allowances for leap years.
 * Because this class is only to be used for WWE stock analysis, dates should come after 1998,
 * as WWE went public in 1998.
 *
 * @author Cal Crosby
 */
case class Date(year: Int, month: Int, day: Int) extends Ordered[Date] {

  
  require(year > 1998, "Year should be greater than 1998") // WWE went public in 1999
  require(1 <= month && month <= 12, "Invalid value for month")

  // This defines which days are valid arguments
  require(1 <= day, "Day should be greater than zero")
  month match {
    case 2 => // February has a special case for leap years
      if year % 4 == 0 then require(day <= 29, "For a leap year, February days should be between 1-29")
      else require(day <= 28, "February days should be between 1-28")
    case 1 | 3 | 5 | 7 | 8 | 10 | 12 => require(day <= 31, "Invalid day entered")
    case 4 | 6 | 9 | 11 => require(day <= 30, "Invalid day entered")
  }

  
  override def toString: String = f"$year-$month%02d-$day%02d"

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