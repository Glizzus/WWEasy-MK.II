object Main extends App {


  val stockList: LinkedList[Date] = FileProcessor.stockDataFromCSV("src/Resources/all_time_by_month.csv")


  val ppvList: LinkedList[Date] = FileProcessor.PPVDataFromCSV("src/Resources/ppv_dates.csv")



  val stock = new StockData(2022, 02, 19, 35.28, 38.69, 33.23, 38.15, 36.839993, 19497000)

  val ec = new PPVData(2022, 02, 19, "Elimination Chamber")
  print(ec.compare(new PPVData(2022, 02, 19, "piss")) == 0) // true
  print(ec.compare(stock) == 0) //true
  print(ec.equals(ec)) //false
  print(ec.equals(new PPVData(2022, 02, 19, "piss"))) //false








}