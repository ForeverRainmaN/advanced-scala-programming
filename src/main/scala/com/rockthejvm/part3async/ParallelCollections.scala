package com.rockthejvm.part3async

import scala.annotation.tailrec
import scala.collection.parallel.*
import scala.collection.parallel.CollectionConverters.*
import scala.collection.parallel.immutable.ParVector

object ParallelCollections {

  val aList = (1 to 1000000).toList
  val anIncrementedList = aList.map(_ + 1)
  val parList: ParSeq[Int] = aList.par
  val aParallelizedIncremntedList = parList.map(_ + 1) // map, flatMap, filter, foreach, reduce, fold etc
  /*
    Applicable for
    - Seq
    - Vectors
    - Arrays
    - Maps
    - Sets

    Use-case: faster processing
   */

  // parallel collection build explicitly

  val aParVector = ParVector[Int](1, 2, 3, 4, 5, 6)

  def measure[A](expression: => A): Long = {
    val time = System.currentTimeMillis()
    expression
    System.currentTimeMillis() - time
  }

  def compareListTransformation(): Unit = {
    val list = (1 to 30000000).toList
    println("list creation done")

    val serialTime = measure(list.map(_ + 1))
    println(s"serial time: $serialTime")


    val parallelTime = measure(list.par.map(_ + 1))
    println(s"parallel time: $parallelTime")
  }

  def demoUndefinedOrder(): Unit = {
    val list = (1 to 1000).toList
    val reduction = list.reduce(_ - _) // [1,2,3].reduce(_ - _) = 1 - 2 - 3 = -4
    val parallelReduction = list.par.reduce(_ - _)

    println(s"Sequential reduction $reduction")
    println(s"Parallel reduction $parallelReduction")
  }

  // for associative ops, result is deterministic
  def demoDefinedOrder(): Unit = {
    val strings = "I love parallel collections but I must be careful".split(" ").toList
    val concatenation = strings.reduce(_ + " " + _)
    val parallelConcatenation = strings.par.reduce(_ + " " + _)

    println(s"Sequential: $concatenation")
    println(s"Parallel: $parallelConcatenation")
  }

  def demoRaceConditions(): Unit = {
    var sum = 0
    (1 to 1000).toList.par.foreach(x => sum += x)
    println(sum)
  }

  def main(args: Array[String]): Unit = {
    demoRaceConditions()
  }
  /*

   */
}
