package com.rockthejvm.part5ts

object PathDependedntTypes {

  class Outer {
    class Inner

    object InnerObject

    type InnerType

    def process(arg: Inner) = println(arg)

    def processGeneral(arg: Outer#Inner) = println(arg)
  }

  val outer = new Outer
  val inner = new outer.Inner // outer.Inner is a separate TYPE = path-dependent type

  val outerA = new Outer
  val outerB = new Outer
  //val inner2: outerA.Inner = new outerB.Inner // path-dependent types are DIFFERENT
  val innerA = new outerA.Inner
  val innerB = new outerB.Inner

  // parent-type: Outer#Inner
  outerA.processGeneral(innerA) // ok
  outerA.processGeneral(innerB) // still ok

  /*
    why:
      - type-checking/type inference, e.g. Akka Streams: Flow[Int, Int, NotUsed]#Repr
      - type-level programming
   */

  // methods with dependent types: return a different COMPILE-TIME type depending on the argument

  trait Record {
    type Key

    def defaultValue: Key
  }

  class StringRecord extends Record {
    override type Key = String

    override def defaultValue: String = ""
  }

  class IntRecord extends Record {
    override type Key = Int

    override def defaultValue: Int = 0
  }

  def getDefaultIdentifier(record: Record): record.Key = record.defaultValue

  val aString = getDefaultIdentifier(new StringRecord) // a string
  val anInt = getDefaultIdentifier(new IntRecord) // an int, ok

  // function with dependent types
  val getIdentifierFunc: Record => Record#Key = getDefaultIdentifier


  def main(args: Array[String]): Unit = {

  }
}
