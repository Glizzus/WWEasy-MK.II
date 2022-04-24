import org.scalatest.funsuite.AnyFunSuite

class DateTests extends AnyFunSuite {

  test("The constructor correctly constructs a valid date") {
    val date = new Date(2002, 02, 04)
    assert(date.year === 2002)
    assert(date.month === 02)
    assert(date.day === 04)
  }

  test("The constructor should not accept any year before 1999") {
    intercept[IllegalArgumentException] { Date(1995, 02, 02) }
  }

  test("The constructor should not accept any month less than 1") {
    intercept[IllegalArgumentException] { Date(2000, 0, 02) }
    intercept[IllegalArgumentException] { Date(2000, -1, 02) }
    intercept[IllegalArgumentException] { Date(2000, Int.MinValue, 02) }
  }

  test("The constructor should not accept any month greater than 12") {
    intercept[IllegalArgumentException] { Date(2000, 13, 02) }
    intercept[IllegalArgumentException] { Date(2000, Int.MaxValue, 02) }
  }

}
