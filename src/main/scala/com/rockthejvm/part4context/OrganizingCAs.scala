package com.rockthejvm.part4context

object OrganizingCAs {

  val aList = List(2, 3, 1, 4)
  val anOrderedList = aList.sorted

  // compiler fetches givens/EMs


  //  1 - local scope
  //  2 - imported scope
  //  3 - companions of all types involved in method signature
  /*
    - Ordering
    - List
    - Person
   */

  case class Person(name: String, age: Int)

  val persons = List(
    Person("Steve", 30),
    Person("Amy", 22),
    Person("John", 67)
  )

  object PersonGivens {
    given ageOrdering: Ordering[Person] with {
      override def compare(x: Person, y: Person) = y.age - x.age
    }
  }

  // a - import explicitly
  //import PersonGivens.ageOrdering

  // b - import a given for a particular type
  //import PersonGivens.{ given Ordering[Person] }

  // c - import all givens

  import PersonGivens.given

  //warning: import PersonGivens.* does NOT also import given instances!
  val sortedPerson = persons.sorted

  /*
   =============================== TIPS ====================================
    1) When you have a "default" given (only ONE that makes sense) add it in the companion of the type
    2) When you have MANY possible givens, but ONE that is dominant, add that in the companion object and the rest in other objects
    3) When you have MANY possible givens and NO ONE is dominant, add them in separate objects and import them explicitly
  ===========================================================================

  Same principles apply to extension methods as well
   */

  case class Purchase(nUnits: Int, unitPrice: Double)

  object Purchase {
    given totalPriceOrderingDesc: Ordering[Purchase] with {
      override def compare(x: Purchase, y: Purchase): Int =
        val xTotalPrice = x.nUnits * x.unitPrice
        val yTotalPrice = y.nUnits * y.unitPrice

        if (xTotalPrice == yTotalPrice) 0
        else if (xTotalPrice < yTotalPrice) -1
        else 1
    }
  }

  object PurchaseGivens {
    given unitCountOrderingDesc: Ordering[Purchase] = Ordering.fromLessThan((x, y) => y.nUnits > x.nUnits)

    given unitPriceOrderingAsc: Ordering[Purchase] = Ordering.fromLessThan((x, y) => x.unitPrice < y.unitPrice)
  }

  def main(args: Array[String]): Unit = {

  }
}
