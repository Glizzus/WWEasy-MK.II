object Main extends App {

  val list = FileProcessor.StockDataFromCSV("src/Resources/all_time_by_month.csv")
  print(list.toString)



  }


