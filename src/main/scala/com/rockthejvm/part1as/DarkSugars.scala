package com.rockthejvm.part1as

import scala.annotation.targetName
import scala.util.Try

object DarkSugars {
  // 1 sugar for methods with one argument
  def singleArgMethod(arg: Int): Int = arg + 1

  val aMethodCall = singleArgMethod({
    42
  })

  val aMethodCall2 = singleArgMethod {
    42
  }

  val aTryInstance = Try {
    throw new RuntimeException
  }

  val anIncrementedList = List(1, 2, 3).map { x => x + 1 }

  // 2 - single abstract method pattern (since scala 2.12)
  trait Action {
    def act(x: Int): Int
  }

  val anAction = new Action {
    override def act(x: Int): Int = x + 1
  }

  val anotherAction: Action = (x: Int) => x + 1 // new Action { def act(x: Int) = x + 1 }
  anotherAction.act(2)

  // example: Runnable
  val aThread = new Thread(new Runnable {
    override def run(): Unit = println("Hi, Scala, from another thread")
  })

  val aSweeterThread = new Thread(() => println("Hi, Scala!"))

  // 3 - methods ending in a : are RIGHT-ASSOCIATIVE
  val aList = List(1, 2, 3)
  val aPrependedList = 0 :: aList
  val aBigList = 0 :: 1 :: 2 :: 3 :: List(3, 4) // List(3,4).::(2).::(1).::(0)

  class MyStream[T] {
    infix def -->:(value: T): MyStream[T] = this
  }

  val myStream = 1 -->: 2 -->: 3 -->: 4 -->: new MyStream[Int]


  // 4 - multi-word identifiers

  class Talker(name: String) {
    infix def `and then said`(gossip: String) = println(s"$name said $gossip")
  }

  val daniel = new Talker("Daniel")
  val danielsStatement = daniel `and then said` "I love Scala"


  // example: HTTP libraries
  object `Content-Type` {
    val `application/json` = "application/JSON"
  }

  // 5 - infix types
  @targetName("Arrow") // for more readable bytecode + Java interop
//  infix class -->[A, B]

//  val compositeType: Int --> String = new -->[Int, String]

  // 6 - update()
  val anArray = Array(1, 2, 3, 4)
  anArray.update(2, 45)
  anArray(2) = 45 // same

  // 7 - mutable fields
  class Mutable {
    private var internalMember: Int = 0

    def member = internalMember // "getter"

    def member_=(value: Int): Unit =
      internalMember = value // "setter"

  }

  val aMutableContainer = new Mutable
  aMutableContainer.member = 42 // aMutableContainer.member_=(42)

  // 8 - variable arguments (varargs)
  def methodsWithVarargs(args: Int*) = {
    // return the number of arguments supplied
    args.length
  }

  val callWithZeroArgs = methodsWithVarargs()
  val callWithOneArgs = methodsWithVarargs(1)
  val callWithTwoArgs = methodsWithVarargs(2, 25)

  val aCollection = List(1, 2, 3, 4)
  val callWithDynamicArgs = methodsWithVarargs(aCollection *)
}
