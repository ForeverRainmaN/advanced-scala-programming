package com.rockthejvm.part3async

import java.util.concurrent.Executors

object JVMConcurrencyProblems {

  def runInParallel(): Unit = {
    var x = 0

    val thread1 = new Thread(() => {
      x = 1
    })

    val thread2 = new Thread(() => {
      x = 2
    })

    thread1.start()
    thread2.start()

    println(x) // race condition
  }

  case class BankAccount(var amount: Int)

  def buy(account: BankAccount, thing: String, price: Int): Unit = {
    /*
      Involves three steps:
      - read old value
      - compute result
      - write new value
     */
    account.amount -= price
  }

  def buySafe(account: BankAccount, thing: String, price: Int): Unit = {
    account.synchronized { // does not allow multiple threads to run the critical section AT THE SAME TIME
      account.amount -= price // critical section
    }
  }

  /*
    Example race condition:
  thread1 (shoes)
    - reads amount 50000
    - compute result 50000 - 3000 = 47000
  thread2 (iPhone)
    - reads amount 50000
    - compute result = 50000 - 4000 = 46000
  thread1 (shoes)
    - write amount 47000
  thread2 (iPhone)
    - write amountt 46000
   */

  def demoBankingProblem(): Unit = {
    (1 to 10000).foreach { _ =>
      val account = BankAccount(50000)
      val threadOne = new Thread(() => buy(account, "shoes", 3000))
      val threadTwo = new Thread(() => buy(account, "iPhone", 4000))

      threadOne.start()
      threadTwo.start()

      threadOne.join()
      threadTwo.join()

      if (account.amount != 43000) println(s"AHA, I've just broken the bank ${account.amount}")
    }
  }

  def inceptionThreadsDemo(): Unit = {
    def createNestedThread(i: Int): Thread =
      if (i == 0) new Thread(() => {
        println(s"Hello from thread $i")
      }) else
        new Thread(() => {
          println(s"Hello from thread $i")
          createNestedThread(i - 1).start()
        })

    createNestedThread(50).start()
  }

  def inceptionThreads(maxThreads: Int, i: Int): Thread =
    new Thread(() => {
      if (i < maxThreads) {
        val newThread = inceptionThreads(maxThreads, i + 1)
        newThread.start()
        newThread.join()
      }

      println(s"Hello from thread $i")
    })

  def minMaxX(): Unit = {
    var x = 0
    val threads = (1 to 100).map(_ => new Thread(() => x += 1))
    threads.foreach(_.start())
    println(x)
  }

  /*
    max value = 100 - each thread increases x by 1
    min value = 1
      all threads read x = 0 at the same time
      all threads (in parallel) compute 0 + 1 = 1
      all threads try to write x = 1
   */

  def demoSleepFallacy(): Unit = {
    var message = ""
    val awesomeThread = new Thread(() => {
      Thread.sleep(1000)
      message = "Scala is awesome"
    })

    message = "Scala sux"
    awesomeThread.start()
    Thread.sleep(1001)
    // solution: join the worker thread
    awesomeThread.join()
    println(message)
  }

  /*
    almost always, message = "Scala is awesome"
    is it guaranteed? No
    Obnoxious situation (possible):
      main thread:
        message = "Scala sucks"
        awesomeThread.start()
        sleep(1001) - yields execution
      awesome thread:
        sleep(1000) - yields execution
      OS gives the CPU to some important thread, takes > 2s
      OS gives the CPU back to the main thread
      main thread:
        println(message) // Scala sucks
      awesome thread:
        message = "Scala is awesome"
   */

  def main(args: Array[String]): Unit = {
    demoSleepFallacy()
  }
}
