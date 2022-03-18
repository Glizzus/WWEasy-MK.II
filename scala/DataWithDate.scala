abstract class DataWithDate(val date: String) {

  def compare(that: DataWithDate): Int = this.date.compare(that.date)

}
