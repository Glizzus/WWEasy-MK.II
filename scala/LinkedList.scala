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
   * Returns the size of the LinkedList.
   *
   * @return listSize
   */
  override def size: Int = listSize


  /**
   * Returns whether the LinkedList is empty or not.
   *
   * @return True if empty, false otherwise
   */
  override def isEmpty: Boolean = listSize == 0


  // TODO: make this method more functional

  /**
   * This method adds an element to the LinkedList with regard to the defined order.
   *
   * @param element any element of generic type A that implements Ordered[A]
   */
  override def add(element: A): Unit = {
    if element == null then throw new NullPointerException("Element can not be null")
    val newNode = new Node(element)

    if (isEmpty) {
      front = newNode
      back = newNode
      listSize += 1
    }
    else if (element.compare(front.element) < 0) {
      newNode.next = front
      newNode.prev = null
      front.prev = newNode
      front = newNode
      listSize += 1
    }
    else if (element.compare(back.element) > 0) {
      newNode.next = null
      newNode.prev = back
      back.next = newNode
      back = newNode
      listSize += 1
    }
    //TODO: refactor this, the nesting is ugly and a utility method could be used
    else {

      var walker = front
      while ((walker.next != null) && (walker.element.compare(element) < 0)) {
        walker = walker.next
      }
      newNode.next = walker.next
      newNode.prev = walker
      walker.next.prev = newNode
      walker.next = newNode
      listSize += 1
    }
  }


  // TODO: make this method more functional

  /**
   * Removes an element from the LinkedList. The element must not be null, it must
   * not exist in the LinkedList, and the LinkedList should not be empty.
   *
   * @param element the element to be removed from the LinkedList
   */
  override def remove(element: A): Unit = {
    if element == null then throw new NullPointerException("Element can not be null")
    else if isEmpty then throw new IndexOutOfBoundsException("LinkedList can not be empty")

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
    //TODO: refactor this, the nesting is ugly and a utility method could be used
    else {

      var walker = front
      while ((walker.next != null) && (walker.element.compare(element) < 0)) {
        walker = walker.next
      }
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


  //TODO: make this method more functional (recursion?)
  /**
   * Checks if the given element exists in the LinkedList
   *
   * @param element the element to be searched for
   * @return True if the element exits, false otherwise
   */
  override def contains(element: A): Boolean = {
    if element == null then throw new NullPointerException("Element can not be null")
    var walker = front
    while ((walker.next != null) && (walker.element.compare(element) < 0)) {
      walker = walker.next
    }
    if walker.element.compare(element) == 0 then return true
    false
  }


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

  override def toString: String = {
    val iter = iterator
    var result = ""
    while (iter.hasNext) {
      result += iter.next.toString
      result += "\n"
    }
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
class Node[A](val element: A, var next: Node[A], var prev: Node[A]) {

  def this(e: A) = {
    this(e, null, null)
  }
}



/**
 * A trait for creating a list
 * @tparam A any type
 */
trait List[A] {

  def add(element: A): Unit

  def remove(element: A): Unit

  def contains(element: A): Boolean

  def size: Int

  def isEmpty: Boolean

}
