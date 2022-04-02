object PPVDataManager extends Manager[Date] {

  override def greet(): Unit = {
    println("Welcome to the WWEasy Pay-Per-View Analyzer")
    println("Select [1] to enter a .csv file")
    println("Select [2] to manually enter a Pay-Per-View")
    println("Select [3] to view the current data")
    println("Select [4] to manipulate the current data")
    println("Select [9] to go back")
    println("Select [0] to exit")
  }

  override def enterCSV(file: String): List[Date] = {
    FileProcessor.PPVDataFromCSV(file, false)
  }
}
