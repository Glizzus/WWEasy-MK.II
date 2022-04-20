object Client extends App {

  val map = FileProcessor.ppvCsvToMap("src/main/Resources/ppv_dates.csv")
  print(map)

  val stockMap = FileProcessor.stockCsvToMap("src/main/Resources/all_time_by_month.csv")
  print(stockMap)

  print(stockMap.filterByDate("2005-12-23", '>'))

}
