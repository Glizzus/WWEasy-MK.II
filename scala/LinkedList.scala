class LinkedList[A <: Ordered[A]]
                (var front: Node[A], var back: Node[A], var listSize: Int)
                extends List[A] {

  override def size: Int = listSize

  override def isEmpty: Boolean = listSize == 0

  //TODO: make this method more functional
  /**
   * This method adds an element to the LinkedList with regard to the defined order.
   * The element can not be null, and it also must not exist in the list already.
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
    else if (element.compareTo(front.element) < 0) {
      newNode.next = front
      newNode.prev = null
      front.prev = newNode
      front = newNode
      listSize += 1
    }
    else if (element.compareTo(back.element) > 0) {
      newNode.next = null
      newNode.prev = back
      back.next = newNode
      back = newNode
      listSize += 1
    }
      //TODO: refactor this, the nesting is ugly and a utility method could be used
    else {

      var walker = front
      while ((walker.next != null) && (walker.element.compareTo(element) < 0)) {
        walker = walker.next
      }
      if ((walker.next == null) || (walker.element.compareTo(element) == 0)) {
        throw new IllegalArgumentException("Element already exists in list")
      }
      else {
        newNode.next = walker.next
        newNode.prev = walker
        walker.next.prev = newNode
        walker.next = newNode
        listSize += 1
      }
    }
  }

  override def remove(element: A): Unit = {} //To Do

  override def contains(element: A): Boolean = {false} //To Do


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
