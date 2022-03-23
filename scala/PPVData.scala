/**
 * Data that includes PPV titles and their corresponding dates.
 * Any Wrestlemania that occurs over 2 days has its date set to its 1st day.
 * This data was scraped from https://tjrwrestling.net/every-wwe-pay-per-view-review/
 *
 * @author Cal Crosby
 * 
 * @param year the year of the date in which the data was taken
 * @param month the month of the year in which the data was taken
 * @param day the day of the year in which the data was taken
 *            
 * @param ppv name of the PPV
 */
class PPVData(val year: Int, val month: Int, val day: Int, val ppv: String)
              extends Date { //TODO: Consolidate parameters


  /**
   * Checks if an object is eligible to be checked for equality.
   *
   * @param obj any object
   * @return true if checking is allowed, false otherwise
   */
  def canEqual(obj: Any): Boolean = obj.isInstanceOf[StockData]


  /**
   * Checks if two PPVData objects are equal. Unlike compare(), which
   * only checks the date, this method checks every field.
   *
   * @param that the PPVData object to be compared
   * @return true if equal, false otherwise
   */
  override def equals(that: Any): Boolean =
    that match {
      case that: PPVData => {
        that.canEqual(this) &&
          this.compare(that) == 0 &&
          this.ppv == that.ppv
      }
      case _ => false
    }
  
  
  /**
   * Compares two PPVData objects in ascending order of date.
   * Unlike equals(), which checks every field, this only compares dates.
   * 
   * @param that the second DataWithDate object to be compared to
   *  @return A negative integer if this < that, 0 if this == that, a positive integer otherwise
   */
  override def compare(that: Date): Int = super.compare(that)

  /**
   * Formats a PPVData object as a String on one line
   * @return PPVData as a String
   */
  override def toString: String = {
    s"${super.toString}   $ppv"
  }
}
