trait Manager[A] {

  def greet(): Unit
  
  def enterCSV(file: String): List[A]
}
