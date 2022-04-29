/** Encapsulates data about ratings for televised shows.
 * 
 * @author Cal Crosby
 * @param title the title of the show
 * @param absRating the absolute rating (estimated number of viewers) of the show
 */
case class RatingsData(title: String, relRating: Float, absRating: Int) {

  override def toString: String = {
    f"$title%-60s" +
      f"${if relRating == -1 then " NaN" else f"$relRating%1.2f"}     " +
      f"${if absRating == -1 then " NaN" else absRating}"
  }

  val isRaw: Boolean = title.contains("RAW")
  val isSmackDown: Boolean = title.contains("SmackDown")
  val isNxt: Boolean = title.contains("NXT")

}

