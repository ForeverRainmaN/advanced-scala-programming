package com.rockthejvm.part2afp

object CurryingPAFs {

  // currying
  val superAdder: Int => Int => Int =
    x => y => x + y

  val add3: Int => Int = superAdder(3)
  val eight = add3(5) // 8
  val eight_v2 = superAdder(3)(5)

  // curried methods
  def curriedAdder(x: Int)(y: Int): Int =
    x + y

  // methods != function values

  // converting methods to functions = eta-expansion
  val add4 = curriedAdder(4)
  val nine = add4(5) // 9

  private def increment(x: Int): Int = x + 1

  val aList = List(1, 2, 3)
  val anIncrementedList = aList.map(increment)

  // underscores are powerful: allow you to decide the shapes of lambdas obtained from methods
  def concatenator(a: String, b: String, c: String): String = a + b + c

  val insertName = concatenator(
    "Hello, my name is ",
    _: String,
    ", I'm going to show you a nice scala trick"
  ) // x => concatenator("...", x, "...")

  val danielsGreeting = insertName("Daniel")

  val fillInTheBlanks = concatenator(_: String, "Daniel", _: String)
  val danielsGreeting_v2 = fillInTheBlanks("Hi,", "how are you?")

  /*
    Exercises:
   */

  val simpleAddFunction = (x: Int, y: Int) => x + y

  def simpleAddMethod(x: Int, y: Int) = x + y

  def curriedMethod(x: Int)(y: Int) = x + y

  def add7 = (x: Int) => simpleAddFunction(x, 7)

  val add7_1 = simpleAddMethod(_, 7)
  val add7_2 = curriedMethod(7)
  val add7_3 = curriedMethod(_: Int)(7)
  val add7_4 = simpleAddFunction(_, 7)
  val add7_5 = simpleAddFunction.curried(7)

  private def myFormatter(fmt: String)(v: Double): String =
    fmt.format(v)

  // methods vs functions + by-name vs 0-lambdas
  def byName(n: => Int) = n + 1
  def byLambda(f: () => Int) = f() + 1

  def method: Int = 42
  def parenMethod(): Int = 42

  byName(23)  // ok
  byName(method) // 43. eta-expanded? NO - method is invoked here
  byName(parenMethod()) // simple
  // byName(parenMethod) // not ok
  byName((() => 42)()) // ok
  // byName(() => 42) // not ok

  // byLambda(23) // not ok
  // byLambda(method) // eta expansion is NOT possible
  byLambda(parenMethod) // eta expansion is done
  byLambda(() => 42) // ok
  byLambda(() => parenMethod()) // ok


  def main(args: Array[String]): Unit = {
  }
}
