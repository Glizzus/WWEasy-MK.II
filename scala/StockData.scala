class StockData(override val date: String, val open: Float,
                val high: Float, val low: Float, val close: Float,
                val adjClose: Float, val volume: Int) extends DataWithDate(date) {
  
  
  
}
