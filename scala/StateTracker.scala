import scala.collection.mutable
import scala.collection.mutable.Stack

/**
 * This class currently keeps track of state across the application.
 * Because this goes against all functional programming rules, this is a prime candidate for deletion.
 */
object StateTracker {

  // Keeps track of inputs so that the user can undo changes
  val inputTracker: mutable.Stack[Int] = new mutable.Stack[Int]

  // Keeps track of stockData manipulations so that the user can undo changes
  val stockDataHistory: mutable.Stack[List[Date]] = new mutable.Stack[List[Date]]
  stockDataHistory.push(List[Date]())

  // Keeps track off ppvData manipulations so that the user can undo changes
  val ppvDataHistory: mutable.Stack[List[Date]] = new mutable.Stack[List[Date]]
  ppvDataHistory.push(List[Date]())
}
