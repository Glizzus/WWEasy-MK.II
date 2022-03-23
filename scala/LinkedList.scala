import scala.annotation.tailrec
import scala.collection.Iterator

/**
 * A custom LinkedList. It is linked because it was intended to be
 * easy to merge two lists together. In the future, this may be replaced
 * with a standard Scala collection.
 *
 * @param front the first Node of the LinkedList
 * @param back the last Node of the LinkedList
 * @param listSize the size of the LinkedList
 * @tparam A any type
 */
class LinkedList[A <: Ordered[A]]
                (var front: Node[A], var back: Node[A], var listSize: Int)
                extends List[A] {


  /**
   * Auxiliary constructor that instantiates empty LinkedList[A].
   */
  def this() = {
    this(null, null, 0)
  }


  /**
   * Returns the size of the LinkedList.
   * @return listSize
   */
  override def size: Int = listSize


  /**
   * Returns whether the LinkedList is empty or not.
   * @return True if empty, false otherwise
   */
  override def isEmpty: Boolean = listSize == 0


  /**
   * This method adds an element to the LinkedList with regard to the defined order.
   * I saw an operator override in the Scala source code and thought it was cool.
   * For readability's sake, this may be replaced with a method named add()
   *
   * This runs in worst-case O(N) time, but for sorted data (like most csv files),
   * this runs in O(1) time.
   *
   * @param element any element of generic type A that implements Ordered[A]
   */
  override def += (element: A): Unit = {
    if element == null then throw new NullPointerException("Element can not be null")
    val newNode = new Node(element)

    if (isEmpty) {
      front = newNode
      back = newNode
      listSize += 1
    }
    else if (element.compare(front.element) < 0) {  //Appends to beginning of List in O(1)
      newNode.next = front
      newNode.prev = null
      front.prev = newNode
      front = newNode
      listSize += 1
    }
    else if (element.compare(back.element) > 0) {  //Appends to end of List in O(1)
      newNode.next = null
      newNode.prev = back
      back.next = newNode
      back = newNode
      listSize += 1
    }
    else {
      var walker = front
      while ((walker.next != null) && (walker.element.compare(element) < 0)) {
        walker = walker.next
      }
      newNode.next = walker
      walker.prev.next = newNode
      newNode.prev = walker.prev
      walker.prev = newNode
      listSize += 1
    }

  }


  /**
   * Removes an element from the LinkedList. The element must not be null, it must
   * not exist in the LinkedList, and the LinkedList should not be empty.
   * I saw an operator override in the Scala source code and thought it was cool.
   * For readability's sake, this may be replaced with a method named remove()
   *
   * This runs in a worst-case O(N).
   *
   * @param element the element to be removed from the LinkedList
   */
  override def -= (element: A): Unit = {
    if element == null then throw new NullPointerException("Element can not be null")
    else if isEmpty then throw new IllegalArgumentException("LinkedList can not be empty")

    else if (size == 1) {
      front = null
      back = null
      listSize -= 1
    }
    else if (element.compare(front.element) == 0) {
      front.next.prev = null
      front = front.next
      listSize -= 1
    }
    else if (element.compare(back.element) == 0) {
      back.prev.next = null
      back = back.prev
      listSize -= 1
    }
    else {

      var walker = front
      while ((walker.next != null) && (walker.element.compare(element) < 0)) {
        walker = walker.next
      }
      //We could call contains(element), but doing this saves a O(N) method call.
      if ((walker.next == null) || (walker.element.compare(element) > 0)) {
        throw new IllegalArgumentException("Element does not exist in the LinkedList")
      }
      else {
        walker.prev = walker.prev.prev
        walker.prev.next = walker
        listSize -= 1
      }
    }
  }


  /**
   * Checks if the given element exists in the LinkedList.
   * This employs tail recursion, which saves stack frames.
   *
   * This runs in worst-case O(N)
   * 
   * @param element the element to be searched for
   * @return True if the element exits, false otherwise
   */
  def contains(element: A): Boolean = {
    @tailrec
    def helper(element: A, node: Node[A]): Boolean = {
      if listSize == 0 then false
      else if node.element.compare(element) == 0 then true
      else helper(element, node.next)
    }
    helper(element, front)
  }


  /**
   * Creates an iterator object to make things like string-building easier.
   * @return iterator and iterator on a LinkedList
   */
  def iterator: Iterator[A] = {
    new Iterator[A] {
      var cursor: Node[A] = front

      override def hasNext: Boolean = cursor != null

      override def next(): A = {
        if hasNext then {
          val elem = cursor.element
          cursor = cursor.next
          elem
        }
        else throw new NullPointerException()
      }
    }
  }


  /**
   * A method that makes a copy of a list without mutating the original list
   * @return copy the list that was copied.
   */
  def copyList: LinkedList[A] = {
    val copy = new LinkedList[A](front = null, back = null, listSize = 0)
    val iter = iterator
    while (iter.hasNext) {
      copy += iter.next
    }
    copy
  }


  /**
   * A method that returns the merging of two lists, without
   * mutating the original lists.
   *
   * @param that the list to be merged with this list
   * @return a list that is the merging of two lists
   */
  def merge(that: LinkedList[A]): LinkedList[A] = {

    val copy1 = this.copyList
    val copy2 = that.copyList
    if (copy1.size < copy2.size) { // Merges the smaller list onto the larger list
      val iter = copy1.iterator
      while (iter.hasNext) {
        copy2 += iter.next
      }
      copy2
    }
    else { // Merges the smaller list onto the larger list
      val iter = copy2.iterator
      while (iter.hasNext) {
        copy1 += iter.next
      }
      copy1
    }
  }


  /**
   * Formats a LinkedList as a String, with each object
   * represented as a String on its own line
   * @return the LinkedList as a String
   */
  override def toString: String = {
    val iter = iterator
    var result = ""
    while (iter.hasNext) {
      result += iter.next.toString
      result += "\n"
    }
    result
  }


  /**
   * An alternative toString method for use with headers.
   * 
   * @param header the csv header to be added to the beginning of the String
   * @return The header and LinkedList[A] formatted as a String
   */
  def toString(header: String): String = {
    var result = s"$header\n"
    result += this.toString
    result
  }
}



/**
 * Defines a custom Node class for use in a LinkedList
 *
 * @param element the element to be encapsulated in the Node
 * @param next the next Node that this Node points to
 * @param prev the previous Node that this Node points to
 * @tparam A any type
 */
private class Node[A](val element: A, var next: Node[A], var prev: Node[A]) {

  def this(e: A) = {
    this(e, null, null)
  }
}



/**
 * A trait for creating a list.
 * I don't know why I made a trait. Maybe I'll make more custom collections later.
 *
 * @tparam A any type
 */
sealed trait List[A] {

  /**
   * Adds an element to a List
   * @param element the element to add to the List
   */
  def += (element: A): Unit

  
  /**
   * Removes an element from a List
   * @param element the element to remove from the List
   */
  def -= (element: A): Unit


  /**
   * Checks if a List contains an element
   * @param element the element to be checked for in the List
   * @return true if the element is found, false otherwise
   */
  def contains(element: A): Boolean


  /**
   * Returns the size of a List
   * @return the size of the List
   */
  def size: Int


  /**
   * Returns whether a List is empty or not
   * @return true if the list is empty, false otherwise
   */
  def isEmpty: Boolean
}
