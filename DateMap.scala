import scala.annotation.targetName
import scala.collection.immutable.TreeMap
import java.text.SimpleDateFormat

case class DateMap(data: TreeMap[Date, Any]) {


  @targetName("put")
  def + (key: Date, any: Any): Map[Date, Any] = {
    data + (key->any)
  }
  
  override def toString: String = data.mkString("\n")

  def filterByDate(strDate: String, operator: Char): DateMap = {
    val date = parseDate(strDate)
    operator match {
      case '<' => DateMap(data.filter((k, _) => k.compare(date) < 0))
      case '>' => DateMap(data.filter((k, _) => k.compare(date) > 0))
      case '=' => DateMap(data.filter((k, _) => k.equals(date)))
      case _ => throw new IllegalArgumentException()
    }
  }

  def parseDate(date: String): Date = {
    FileProcessor.parseDate(date)
  }


}
