package com.rockthejvm.part3async

import java.util.concurrent.Executors
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

object Futures {

  def calculateMeaningOfLife(): Int = {
    // simulate long compute
    Thread.sleep(1000)
    42
  }

  // thread pool (Java-specific)
  val executor = Executors.newFixedThreadPool(4)

  // thread pool (Scala-specific)
  given executionContext: ExecutionContext = ExecutionContext.fromExecutorService(executor)

  // a future = an async computation that will finish at some point
  val aFuture = Future(calculateMeaningOfLife())

  val futureInstantResult: Option[Try[Int]] = aFuture.value

  // callbacks
  aFuture.onComplete {
    case Success(value) => println(s"I've completed with the meaning of life: $value")
    case Failure(exception) => println(s"My async computation failed: $exception")
  } // on SOME other thread

  def main(args: Array[String]): Unit = {
    println(futureInstantResult) // Inspect the value of the future right now
    Thread.sleep(2000)
    executor.shutdown()
  }
}
