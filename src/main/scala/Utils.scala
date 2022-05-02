object Utils {
  
  def spinOffThread(block: => Unit): Unit = {
    new Thread {
      override def run(): Unit = block
    }.start()
  }
  
  
  
  
  
}
