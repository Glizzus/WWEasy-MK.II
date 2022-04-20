case class StockData(open: Float, high: Float, low: Float, close: Float,
                     adjClose: Float, volume: Int) {

  override def toString: String = {
    f"$open%2.3f   $high%2.3f   $low%2.3f   $close%2.3f   $adjClose%2.3f   $volume"
  }








}
