import scala.collection.immutable.HashMap

/** Encapsulates the State of the program so that it can run recursively.
 *  
 * @author Cal Crosby
 * @param maps Each DateMap created by the user, stored in a Vector for fast random access
 */
case class State(maps: HashMap[String, DateMap] = HashMap[String, DateMap]()) 

  

