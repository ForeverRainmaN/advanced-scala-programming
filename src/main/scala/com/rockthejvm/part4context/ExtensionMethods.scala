package com.rockthejvm.part4context

import scala.annotation.tailrec

object ExtensionMethods {

  case class Person(name: String) {
    def greet: String = s"Hi, my name is $name, nice to meet you!"
  }

  extension (string: String)
    def greetAsPerson: String = Person(string).greet

  val danielGreeting = "Daniel".greetAsPerson

  // generic extension methods
  extension [A](list: List[A])
    def ends: (A, A) = (list.head, list.last)

  val aList = List(1, 2, 3, 4)
  val firstLast = aList.ends

  // reason: make APIs very expressive
  // enhance CERTAIN types with new capabilities
  // super-powerful code

  trait Combinator[A] {
    def combine(x: A, y: A): A
  }

  extension [A](list: List[A])
    def combineAll(using combinator: Combinator[A]): A =
      list.reduce(combinator.combine)

  given intCombinator: Combinator[Int] with {

    override def combine(x: Int, y: Int): Int = x + y
  }

  val firstSum = aList.combineAll // works, sum is 10

  val someString = List("I", "Love", "Scala")
  val stringsSum = someString

  // grouping extensions

  object GroupedExtensions {
    extension [A](list: List[A]) {
      def ends: (A, A) = (list.head, list.last)
      def combineAll(using combinator: Combinator[A]): A =
        list.reduce(combinator.combine)
    }
  }

  extension (n: Int) {
    def isPrime: Boolean = {
      @tailrec
      def go(divisor: Int): Boolean = {
        if (divisor > n / 2) true
        else if (n % divisor == 0) false
        else go(divisor + 1)
      }

      assert(n >= 0)
      if (n == 0 || n == 1) false
      else go(2)
    }
  }

  sealed abstract class Tree[A]

  case class Leaf[A](value: A) extends Tree[A]

  case class Branch[A](left: Tree[A], right: Tree[A]) extends Tree[A]

  extension [A](tree: Tree[A]) {
    def map[B](f: A => B): Tree[B] =
      tree match
        case Leaf(value) => Leaf(f(value))
        case Branch(left, right) => Branch(left.map(f), right.map(f))

    def sum(using combinator: Combinator[A]): A =
      tree match
        case Leaf(value) => value
        case Branch(left, right) => combinator.combine(left.sum, right.sum)

    def forAll(predicate: A => Boolean): Boolean =
      tree match
        case Leaf(value) => predicate(value)
        case Branch(left, right) => left.forAll(predicate) && right.forAll(predicate)
  }

  // call extension methods directly
  val firstLast_v2 = ends(aList)

  def main(args: Array[String]): Unit = {
    val tree = Branch(Branch(Leaf(5), Leaf(6)), Leaf(15))
    val mapped = tree.map(x => x + 1)
    println(mapped.sum)
    println(tree.forAll(x => x < 10))
  }

}
