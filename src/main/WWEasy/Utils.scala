object Utils {
  
  def spinOffThread(block: => Unit): Unit = {
    val t = new Thread {
      override def run(): Unit = block
    }
    t.start()
  }
}
