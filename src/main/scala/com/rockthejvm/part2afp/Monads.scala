package com.rockthejvm.part2afp

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
    val f = (x: Int) => Option(x, x + 1)

    val g = (x: Int) => Option(x, 2 * x)
    val pure = (x: Int) => Option(x)
    val anOption = Option(42)

    // prop 1: left identity
    // Monad[x].flatMap(f) == f(x)
    val leftIdentity = pure(42).flatMap(f) == f(42) // for every x for every f

    // prop 2: right identity
    // Monad[v].flatMap(x => Monad[x]) == Monad[v]
    val rightIdentity = Option.flatMap(pure) == anOption // for every list

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
  def main(args: Array[String]): Unit = {

  }
}
