object Main extends App {

  val stockList: LinkedList[DataWithDate] = FileProcessor.stockDataFromCSV("src/Resources/all_time_by_month.csv")


  val ppvList: LinkedList[DataWithDate] = FileProcessor.PPVDataFromCSV("src/Resources/ppv_dates.csv")


  val merged = stockList.merge(ppvList)
  print(merged.toString)






  }


