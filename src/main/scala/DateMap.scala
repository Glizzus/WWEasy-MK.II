import scala.annotation.{tailrec, targetName}
import scala.collection.immutable.{HashMap, ListSet, TreeMap}
import java.time.LocalDate

/** Wraps TreeMap to manipulate data defined by Dates.
 *
 * Any object is allowed to be the value of the TreeMap, as long as the key is a Date.
 * @author Cal Crosby
 * @param data a TreeMap where the key is a Date, and the value can be Any
 */
case class DateMap(data: TreeMap[LocalDate, Any]) {

  // This just employs the standard toString of a Map and separates each entry by newline
  override def toString: String = data.mkString("\n")


  /** Filters a DateMap based on a date and a relational operator <, =, >.
   *
   * A date can be input with wildcards "_" to match everything.
   * For example, an input "&#95;&#95;&#95;&#95;"-04-&#95;&#95;" matches everything in the month of April, regardless
   * of the month or year. This employs a number of nested methods to achieve this which will be explained
   * in further detail.
   *
   * @param strDate the date to filter by as a String
   * @param operator the relational operator to filter with
   * @throws IllegalArgumentException if the relational operator is not <, =, >
   * @return A new DateMap with only the filtered values.
   */
  def filterByDate(strDate: String, operator: String): DateMap = {

    /** Creates a truth table of what fields to filter the DateMap by.
     *
     * The fields include year, month, and day.
     * These fields are pushed onto a List which is queried later.
     *
     * @param strDate The date to be parsed as a String
     * @return A List containing the fields to filter the DateMap by.
     */
    def getFieldsToFilterBy(strDate: String): List[String] = {

      val wildCards = isEachFieldConcrete(strDate)
      wildCards match {

        // The entire date is a wildcard
        case (false, false, false) => throw new IllegalArgumentException()

        // Only filter the dates by the day
        case (false, false, true) => List("day")

        // Only filter the dates by the month
        case (false, true, false) => List("month")

        // Filter the dates by both the month and the day
        case (false, true, true) => List("month, day")

        // Filter the dates based only on the year
        case (true, false, false) => List("year")

        // Filter the dates based on year and day
        case (true, false, true) => List("year, day")

        // Filter the dates based on the year and month
        case (true, true, false) => List("year, month")

        /* This is going to be the most common option, so we optimize this case by only requiring one filter */
        case (true, true, true) => List("all")

      }
    }


    /** Checks to see if the date has Wildcards in its string representation
     *
     * @param strDate the date as a String that might contain Wildcards
     * @return A tuple representing an input combination to be used in a truth table
     */
    def isEachFieldConcrete(strDate: String): (Boolean, Boolean, Boolean) = {
      val fields = strDate.split("-")
      val year = fields(0)
      val month = fields(1)
      val day = fields(2)
      val isUnderscores = (str: String) => str.forall(c => c == '_')

      (isUnderscores(year), isUnderscores(month), isUnderscores(day))
    }

    val fields = getFieldsToFilterBy(strDate)

    // Internally, we represent the "_" as a 1; it all parses the same later on
    lazy val dateToFilterBy = LocalDate.parse(strDate.replace("_", "1"))


    /** Recursively filters the DateMap by its fields.
     *
     * @param list the List containing which fields to filter by
     * @param mapSoFar the map that is recursively being filtered
     * @param dateToFilterBy the date that each DateMap entry is being compared to
     * @param operator a relational operation (<, =, >) determining how the DateMap is filtered
     * @return The final DateMap which has been filtered by all fields required
     */
    @tailrec
    def recursiveFilterer(list: List[String], mapSoFar: DateMap,
                          dateToFilterBy: LocalDate, operator: String): DateMap = {

      list match {
        case Nil => mapSoFar
        case field :: tail =>
          val filtered = mapSoFar.filter((k, _) => compareDatesWithOperators(operator, k, dateToFilterBy, field))
          recursiveFilterer(tail, filtered, dateToFilterBy, operator)
      }
    }

    /** Determines how the date is compared by its operator, as well as what exactly it is compared to.
     *
     * @param operator a relational operation (<, =, >) determining how the Date is compared.
     * @param date1 the first date to compare
     * @param date2 the second date to compare
     * @param field the field to compare it by
     * @return a Boolean representing if the comparison matches up with the operator
     */
    def compareDatesWithOperators(operator: String, date1: LocalDate, date2: LocalDate, field: String): Boolean = {

      val comp = getDateComparison(date1, date2, field)
      operator match {
        case "<" => comp < 0
        case ">" => comp > 0
        case "=" => comp == 0
        case _ => throw new IllegalArgumentException()
      }
    }

    /** Matches the field to determine how the dates are compared.
     *
     * @param date1 the first date to compare
     * @param date2 the second date to compare
     * @param field the field to compare the dates by
     * @return an Int representing the value of the comparison from date1 and date2
     */
    def getDateComparison(date1: LocalDate, date2: LocalDate, field: String): Int = {
      field match {
        case "all" =>
          date1.compareTo(date2)
        case "year" =>
          date1.getYear.compareTo(date2.getYear)
        case "month" =>
          date1.getMonthValue.compareTo(date2.getMonthValue)
        case "day" =>
          date1.getDayOfMonth.compareTo(date2.getDayOfMonth)
      }
    }
    recursiveFilterer(fields, this, dateToFilterBy, operator)
  }


  @targetName("remove")
  def - (key: LocalDate): DateMap = {
    DateMap(data.removed(key))
  }

  /** Merges two DateMaps together.
   *
   * If a key collision occurs, the parameter Map will take precedence over this Map.
   * @param that the DateMap that takes precedence over this DateMap
   * @return A new DateMap which is the result of merging the two DateMaps
   */
  def merge(that: DateMap): DateMap = {
    DateMap(this.data ++ that.data)
  }

  def filter(f: ((LocalDate, Any)) => Boolean): DateMap = {
    DateMap(data.filter(f))
  }

  def isEmpty: Boolean = {
    data.isEmpty
  }
}
