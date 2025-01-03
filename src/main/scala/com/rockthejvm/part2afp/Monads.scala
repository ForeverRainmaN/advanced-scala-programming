package com.rockthejvm.part2afp

import scala.annotation.{tailrec, targetName}

object Monads {

  def listStory(): Unit = {
    val aList = List(1, 2, 3)
    val listMultiply = for {
      x <- List(1, 2, 3)
      y <- List(4, 5, 6)
    } yield x * y

    val listMultiply_2 = List(1, 2, 3).flatMap(x => List(4, 5, 6).map(y => x * y))

    val f = (x: Int) => List(x, x + 1)
    val g = (x: Int) => List(x, 2 * x)
    val pure = (x: Int) => List(x)

    // prop 1: left identity
    // Monad[x].flatMap(f) == f(x)
    val leftIdentity = pure(42).flatMap(f) == f(42) // for every x for every f

    // prop 2: right identity
    // Monad[v].flatMap(x => Monad[x]) == Monad[v]
    val rightIdentity = aList.flatMap(pure) == aList // for every list

    // prop 3: associativity
    // Monad[v].flatMap(f).flatMap(g) == Monad[v].flatMap(x => f(x).flatMap(g))
    val associativity = aList.flatMap(f).flatMap(g) == aList.flatMap(x => f(x).flatMap(g))
  }

  def optionStory(): Unit = {
    val optionString = for {
      lang <- Option("Scala")
      ver <- Option(3)
    } yield s"$lang-$ver"
    // identical
    val optionString_v2 = Option("Scala").flatMap(lang => Option(3).map(ver => s"$lang-$ver"))
    val f = (x: Int) => Option(x + 1)

    val g = (x: Int) => Option(2 * x)
    val pure = (x: Int) => Option(x)
    val anOption = Option(42)

    // prop 1: left identity
    // Monad[x].flatMap(f) == f(x)
    val leftIdentity = pure(42).flatMap(f) == f(42) // for every x for every f

    // prop 2: right identity
    // Monad[v].flatMap(x => Monad[x]) == Monad[v]
    val rightIdentity = anOption.flatMap(pure) == anOption // for every list

    // prop 3: associativity
    // Monad[v].flatMap(f).flatMap(g) == Monad[v].flatMap(x => f(x).flatMap(g))

    /*
      anOption.flatMap(f).flatMap(g) = Option(42).flatMap(x => Option(x + 1)).flatMap(x => Option(2 * x)
      = Option(43).flatMap(x => Option(2 * x)
      = Option(86)

      anOption.flatMap(x => f(x).flatMap(g)) = Option(42).flatMap(x => Option(x + 1).flatMap(y => 2 * y)))
      = Option(42).flatMap(x => 2 * x + 2)
      = Option(86)
     */
    val associativity = anOption.flatMap(f).flatMap(g) == anOption.flatMap(x => f(x).flatMap(g)) // for any option, f and g
  }

  // Monads chain dependant computations
  // PossiblyMonad ~= IO monad :)
  case class PossiblyMonad[A](unsafeRun: () => A) {
    def map[B](f: A => B): PossiblyMonad[B] =
      PossiblyMonad(() => f(unsafeRun()))

    def flatMap[B](f: A => PossiblyMonad[B]): PossiblyMonad[B] =
      PossiblyMonad(() => f(unsafeRun()).unsafeRun())
  }

  object PossiblyMonad {
    @targetName("pure")
    def apply[A](value: => A): PossiblyMonad[A] =
      new PossiblyMonad(() => value)
  }

  def possiblyMonadExample(): Unit = {

    val aPossiblyMonad = PossiblyMonad {
      println("printing my first possibly monad")
      // do some computations
      42
    }

    val anotherPM = PossiblyMonad {
      println("my second pm")
      "Scala"
    }

    // nothing is run untill you implicitly call unsafeRun on result!
    val aForComprehension = for {
      num <- aPossiblyMonad
      lang <- anotherPM
    } yield s"$num-$lang"

  }

  def main(args: Array[String]): Unit = {
    val f = (x: Int) => PossiblyMonad(x + 1)
    val g = (x: Int) => PossiblyMonad(x * 2)
    val pure = (x: Int) => PossiblyMonad(x)

    possiblyMonadExample()

    // left-identity: false (equal results but diff lambda instances
    val leftIdentity = pure(10).flatMap(f) == f(10)

    // right-identity false
    val rightIdentity = PossiblyMonad(10).flatMap(pure) == PossiblyMonad(() => 10)

    // associativity
    val associativity = PossiblyMonad(10).flatMap(f).flatMap(g) == PossiblyMonad(10).flatMap(x => f(x).flatMap(g))

    //    println(leftIdentity)
    //    println(rightIdentity)
    //    println(associativity)
    // ^^ false negative


    // real tests: values produced + side effect ordering
    val leftIdentity_v2 = pure(10).flatMap(f).unsafeRun() == f(10).unsafeRun()
    val rightIdentity_v2 = PossiblyMonad(10).flatMap(pure).unsafeRun() == PossiblyMonad(() => 10).unsafeRun()
    val associativity_v2 = PossiblyMonad(10).flatMap(f).flatMap(g).unsafeRun() == PossiblyMonad(10).flatMap(x => f(x).flatMap(g)).unsafeRun()

    println(leftIdentity_v2)
    println(rightIdentity_v2)
    println(associativity_v2)

    val fs = (x: Int) => PossiblyMonad {
      println("incrementing")
      x + 1
    }

    val gs = (x: Int) => PossiblyMonad {
      println("doubling")
      x * 2
    }

    val associativity_v3 = PossiblyMonad(10).flatMap(fs).flatMap(gs).unsafeRun() == PossiblyMonad(10).flatMap(x => fs(x).flatMap(gs)).unsafeRun()
    println(associativity_v3) // true, side effect ordering!
  }
}
