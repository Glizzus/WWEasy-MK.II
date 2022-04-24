/** Encapsulates data regarding Pay-Per-Views.
 * 
 * @author Cal Crosby
 * @param title the title of the Pay-Per-View
 */
case class PpvData(title: String) {

  override def toString: String = title
  
}
