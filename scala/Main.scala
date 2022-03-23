import FileProcessor.* //bad practice, but it works for now

object Main extends App {


  val stockList: LinkedList[Date] = stockDataFromCSV("src/Resources/all_time_by_month.csv")
  val ppvList: LinkedList[Date] = PPVDataFromCSV("src/Resources/ppv_dates.csv")


  // This works exactly like I wanted it to, but it's implementation is confusing
  val dataWithHeader = makeHeaderDataTuple("src/Resources/all_time_by_month.csv", getCSVHeader, stockDataFromCSV)
  println(dataWithHeader(1).toString(dataWithHeader(0)))







}