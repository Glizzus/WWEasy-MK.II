import scala.annotation.targetName
import scala.collection.immutable.TreeMap

/** Wraps TreeMap to manipulate data defined by Dates.
 *
 * Any object is allowed to be the value of the TreeMap, as long as the key is a Date.
 * @author Cal Crosby
 * @param data a TreeMap where the key is a Date, and the value can be Any
 */
case class DateMap(data: TreeMap[Date, Any]) {

  // This just employs the standard toString of a Map and separates each entry by newline
  override def toString: String = data.mkString("\n")

  /** Filters a DateMap based on a date and a relational operator <, =, >.
   *
   * @param strDate the date to filter by as a String
   * @param operator the relational operator to filter with
   * @throws IllegalArgumentException if the relational operator is not <, =, >
   * @return A new DateMap with only the filtered values.
   */
  def filterByDate(strDate: String, operator: String): DateMap = {
    val date = DataProcessor.parseDate(strDate, '-')
    operator match {
      case "<" => DateMap(data.filter((k, _) => k.compare(date) < 0))
      case ">" => DateMap(data.filter((k, _) => k.compare(date) > 0))
      case "=" => DateMap(data.filter((k, _) => k.equals(date)))
      case _ => throw new IllegalArgumentException()
    }
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

  def filter(f: Any => Boolean): DateMap = {
    DateMap(data.filter(f))
  }

}
