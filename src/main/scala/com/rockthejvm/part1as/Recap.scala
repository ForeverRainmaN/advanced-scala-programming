package com.rockthejvm.part1as

import scala.annotation.tailrec

object Recap {

  val aCondition = false // vals are constants
  val anIfExpression: Int =
    if (aCondition) 42 else 55 // expressions evaluate to a value

  val aCodeBlock: Int = {
    if (aCondition) 10
    else 20
  }

  // types: Int, String, Double, Boolean, Char
  // Unit = () == "void" in other languages
  //   val theUnit = println("Hello, Scala!")

  def aFunction(x: Int): Int = x + 1

  // recursion stack & tail

  @tailrec
  def factorial(n: Int, acc: Int): Int =
    if (n <= 0) acc
    else factorial(n - 1, n * acc)

  class Animal

  class Dog extends Animal

  val aDog: Animal = new Dog

  trait Carnivore {
    infix def eat(a: Animal): Unit
  }

  class Crocodile extends Animal with Carnivore {
    override infix def eat(a: Animal): Unit = println(
      "I am a croc, I eat everything!!"
    )
  }

  // method notation
  val aCroc = new Crocodile
  aCroc.eat(aDog)
  aCroc eat aDog

  // anonymous classes
  val aCarnivore: Unit = {
    infix def eat(a: Animal): Unit = println("I'm a carnivore")
  }

  // generics
  abstract class LList[A] {
    // type A is known inside the implementation
  }

  // singletons and companions

  object LList // companion object, used for instance-independent(static) fields/methods

  // case classes
  case class Person(name: String, age: Int)

  // enums
  enum BasicColors {
    case RED, GREEN, BLUE
  }

  // exceptions and try/catch/finally
  def throwSomeException(): Int =
    throw new RuntimeException

  val aPotentialFailure: Any =
    try {
      // code that may fail
      throwSomeException()
    } catch {
      case e: Exception => "I caught an exception"
    } finally {
      // closing resources
      println("Some important logs")
    }

  // functional programming
  val incrementer = new Function1[Int, Int] {
    override def apply(x: Int): Int = x + 1
  }

  val two = incrementer(1)

  // lambas
  val annonymousIncrementer = (x: Int) => x + 1

  // hofs = higher-order functions
  val anIncrementedList = List(1, 2, 3).map(annonymousIncrementer)

  // for-comprehensions

  val pairs = for {
    number <- List(1, 2, 3)
    char <- List("a", "b")
  } yield s"$number-$char"

  val pairs2 = List(1, 2, 3).flatMap((x) => List("a", "b").map((y) => s"$x-$y"))

  // Scala collections: Seqs, Arrays, Lists, Vectors, Maps, Tuples, Sets

  // Options, Try's

  // pattern matching
  val x = 2
  val order = x match {
    case 1 => "first"
    case 2 => "second"
    case _ => "not important"
  }

  val bob = Person("Bob", 22)

  val gereeting = bob match {
    case Person(n, _) => s"Hi, my name is $n"
    case _            => "Not important"
  }

  // identation tokens
  class BracelessAnimal:
    def eat(): Unit = {
      println("I am eating")
      println("I am Doing Something")
    }

  def main(args: Array[String]): Unit = {
    // println(factorial(10, 1))
    // println(anIncrementedList)
    println(pairs)
    println(pairs2)
  }

}
