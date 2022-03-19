/**
 * Data that includes PPV titles and their corresponding dates.
 * Any Wrestlemania that occurs over 2 days has its date set to its 1st day.
 * This data was scraped from https://tjrwrestling.net/every-wwe-pay-per-view-review/
 *
 * @param date the date of the PPV
 * @param ppv name of the PPV
 */
class PPVData(override val date: String, val ppv: String)
                     extends DataWithDate(date) with Ordered[DataWithDate] {


  /**
   * Compares two PPVData objects in ascending order of date
   *
   * @param that the second DataWithDate object to be compared to
   *  @return A negative integer if this < that, 0 if this == that, a positive integer otherwise
   */
  override def compare(that: DataWithDate): Int = super.compare(that)


  /**
   * Formats a PPVData object as a String on one line
   *
   * @return PPVData as a String
   */
  override def toString: String = {
    var result = date
    result += "   " + ppv
    result
  }
}
