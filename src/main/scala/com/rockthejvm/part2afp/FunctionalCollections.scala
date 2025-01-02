package com.rockthejvm.part2afp

object FunctionalCollections {
  // sets are functions A => Boolean
  val aSet: Set[String] = Set("I", "Love", "Scala")
  val setContainsScala = aSet("Scala")

  // Seq extends PartialFunction[Int, A]
  val aSeq: Seq[Int] = Seq(1, 2, 3, 4)
  val anElement = aSeq(2)
  // val aNonExistingElement = aSeq(100) // throw an OOBException

  // Map[K, V] "extends" PartialFunction[K, V]

  val aPhoneBook = Map[String, Int](
    "Alice" -> 123456,
    "Bob" -> 9876543
  )

  val alicesPhone = aPhoneBook("Alice")
  // val danielsPhone = aPhoneBook("Daniel") // throw a NoSuchElementException

  def main(args: Array[String]): Unit = {

  }
}
