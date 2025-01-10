package com.rockthejvm.part4context

object Implicits {

  // given/using clauses
  trait Semigroup[A] {
    def combine(x: A, y: A): A
  }

  def combineAll[A](list: List[A])(implicit semigroup: Semigroup[A]): A =
    list.reduce(semigroup.combine)

  implicit val intSemigroup: Semigroup[Int] = new Semigroup[Int] {
    override def combine(x: Int, y: Int): Int = x + y
  }

  val sumof10 = combineAll((1 to 10).toList)

  // implicit arg -> using clause
  // impicit val -> given declaration


  // extensions methods
  //  extension (number: Int)
  //    def isEven = number % 2 == 0

  // in scala2 = implicit class
  implicit class MyRichInteger(number: Int) {
    def isEven = number % 2 == 0
  }

  val questionOfMyLife = 23.isEven

  // implicit conversions
  case class Person(name: String) {
    def greet: String = s"Hi, my name is $name"
  }

  // implicit conversion - SUPER DANGEROUS
  implicit def string2Person(x: String): Person = Person(x)

  // implicit def => synthesize NEW implicit values
  implicit def semigroupOfOption[A](implicit semigroup: Semigroup[A]): Semigroup[Option[A]] = new Semigroup[Option[A]] {
    override def combine(x: Option[A], y: Option[A]): Option[A] = for {
      valueX <- x
      valueY <- y
    } yield semigroup.combine(valueX, valueY)
  }

  val danielSaysHi = "Daniel".greet

  /*
    Why implicits will be phased out:
    - the implicit keyword has many different meanings
    - conversions are easy to abuse
    - implicits are very hard to track down while debugging (givens also not trivial, but they are explicitly imported)
   */
  def main(args: Array[String]): Unit = {

  }
}
