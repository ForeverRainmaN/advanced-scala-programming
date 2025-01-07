package com.rockthejvm.part4context

object Givens {

  // list sorting
  val aList = List(4, 3, 2, 1)
  val anOrderedList = aList.sorted // (descendingOrdering)

  //  given descendingOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)

  //  val anInverseOrderingList = aList.sorted(descendingOrdering)

  // custom sorting
  case class Person(name: String, age: Int)

  val people = List(Person("Alice", 29), Person("Sarah", 34), Person("Jim", 23))

  given personOrdering: Ordering[Person] = (x: Person, y: Person) => x.name.compareTo(y.name)

  val sortedPeople = people.sorted

  object PersonAltSyntax {
    given personOrdering: Ordering[Person] with {
      override def compare(x: Person, y: Person): Int =
        x.name.compareTo(y.name)
    }
  }

  // using clauses
  trait Combinator[A] {
    def combine(x: A, y: A): A
  }

  def combineAll[A](list: List[A])(using combinator: Combinator[A]): A =
    list.reduce(combinator.combine)

  given intCombinator: Combinator[Int] with {
    override def combine(x: Int, y: Int): Int = x + y
  }

  val firstSum = combineAll(List(1, 2, 3, 4)) // combinator passed automatically
  //val combineAllPeople = combineAll(people) // does not compile, no Combinator[Person] in scope

  // context bound
  def combineInGroupsOf3[A](list: List[A])(using Combinator[A]): List[A] =
    list.grouped(3).map(group => combineAll(group) /* given Combinator[A] passed by the compiler */).toList

  def combineInGroupsOf3_v2[A: Combinator](list: List[A]) = // A : Combinator => there is a given Combinator[A] in scope
    list.grouped(3).map(group => combineAll(group) /* given Combinator[A] passed by the compiler */).toList

  // synthesize new given instances based on existing ones
  given listOrdering: Ordering[List[Int]] with {
    override def compare(x: List[Int], y: List[Int]) =
      x.sum - y.sum
  }

  val listOfLists = List(List(1, 2), List(1, 1), List(3, 4, 5))
  val nestedListOrders = listOfLists.sorted

  // ...with generics
  given listOrderingBasedOnCombinator[A](using ord: Ordering[A])(using combinator: Combinator[A]): Ordering[List[A]] with {
    override def compare(x: List[A], y: List[A]): Int = ord.compare(combineAll(x), combineAll(y))
  }


  // pass a regular value in place of a given
  val myCombinator = new Combinator[Int]:
    override def combine(x: Int, y: Int): Int = x * y

  val listProduct = combineAll(List(1, 2, 3, 4))(using myCombinator)

  // Exercise
  // given for ordering Option[A] if you can order A
  // val input = [T] => (x: List[TestCase[T]]) => ???
  //  val input = [T] =>


  def main(args: Array[String]): Unit = {
    //    given optOrdering[A](using ordering: Ordering[A]): Ordering[Option[A]] with {
    //      override def compare(x: Option[A], y: Option[A]): Int = (x, y) match {
    //        case (None, None) => 0
    //        case (None, _) => -1
    //        case (_, None) => 1
    //        case (Some(a), Some(b)) => ordering.compare(a, b)
    //      }
    //    }

    given optOrdering2[A: Ordering]: Ordering[Option[A]] with {
      override def compare(x: Option[A], y: Option[A]): Int = (x, y) match {
        case (None, None) => 0
        case (None, _) => -1
        case (_, None) => 1
        case (Some(a), Some(b)) => summon[Ordering[A]].compare(a, b) // fetchGivenValue
      }
    }

    def fetchGivenValue[A](using theValue: A): A = theValue

    val optList = List(Some(10), Some(5), Some(2))
    val sorted = optList.sorted
    println(sorted)
  }
}
