package part2afp

object PartialFunctions {

  val aFunction: Int => Int = x => x + 1

  val aFussyFunction = (x: Int) =>
    if (x == 1) 42
    else if (x == 2) 56
    else if (x == 5) 99
    else throw new RuntimeException("no suitable cases possible")

  val aFussyFunction_v2 = (x: Int) => x match {
    case 1 => 42
    case 2 => 56
    case 5 => 999
  }

  // partial function
  val aPartialFunction: PartialFunction[Int, Int] = { // x => x match { ... }
    case 1 => 42
    case 2 => 56
    case 5 => 999
  }

  // utilities on PFs
  val canCallOn37 = aPartialFunction.isDefinedAt(37)

  val liftedPf = aPartialFunction.lift

  val anotherPf: PartialFunction[Int, Int] = {
    case 45 => 86
  }

  val pfChain = aPartialFunction.orElse[Int, Int](anotherPf)

  //  HOF's accept PFs as arguments
  val aList = List(1, 2, 3, 4)
  val aChangedList = aList.map { // possible coz PartialFunction[A,B] extends Function[A, B]
    case 1 => 4
    case 2 => 3
    case 3 => 45
    case _ => 0
  }

  case class Person(name: String, age: Int)

  val someKids = List(
    Person("Alice", 3),
    Person("Bobbie", 5),
    Person("Jane", 4)
  )

  val kidsGrowingUp = someKids.map {
    case Person(name, age) => Person(name, age + 1)
  }

  def main(args: Array[String]): Unit = {
    println(aPartialFunction(2))
    println(liftedPf(5))
    println(liftedPf(37))
    println(pfChain(45))
  }
}
