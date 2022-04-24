/** Encapsulates data about ratings for televised shows.
 * 
 * @author Cal Crosby
 * @param title the title of the show
 * @param absRating the absolute rating (estimated number of viewers) of the show
 */
case class RatingsData(title: String, absRating: Int) {

  override def toString: String = {
    s"$title   $absRating"
  }

}

