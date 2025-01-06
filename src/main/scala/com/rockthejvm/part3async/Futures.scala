package com.rockthejvm.part3async

import java.util.concurrent.Executors
import scala.concurrent.*
import scala.concurrent.duration.*
import scala.util.{Failure, Random, Success, Try}

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
  //  aFuture.onComplete {
  //    case Success(value) => println(s"I've completed with the meaning of life: $value")
  //    case Failure(exception) => println(s"My async computation failed: $exception")
  //  } // on SOME other thread

  /*
    Functional Composition
   */

  case class Profile(id: String, name: String) {
    def sendMessage(anotherProfile: Profile, message: String) =
      println(s"${this.name} sending message to ${anotherProfile.name}: $message")
  }

  object SocialNetwork {
    // "database"
    val names = Map(
      "rtjvm.id.1-daniel" -> "Daniel",
      "rtjvm.id.2-jane" -> "Jane",
      "rtjvm.id.3-mark" -> "Mark",
    )

    val friends = Map(
      "rtjvm.id.2-jane" -> "rtjvm.id.3-mark"
    )

    val rnd = new Random()

    // "API
    def fetchProfile(id: String): Future[Profile] = Future {
      // fetch something from the database
      Thread.sleep(rnd.nextInt(300)) // simulate the time delay
      Profile(id, names(id))
    }

    def fetchBestFriend(profile: Profile): Future[Profile] = Future {
      Thread.sleep(rnd.nextInt(400))
      val bestFriendId = friends(profile.id)
      Profile(bestFriendId, names(bestFriendId))
    }
  }

  // problem: sending a message to my best friend

  def sendMessageToBestFriend(accountId: String, message: String): Unit = {
    val profileFuture = SocialNetwork.fetchProfile(accountId)
    profileFuture.onComplete {
      case Success(profile) =>
        val friendProfileFuture = SocialNetwork.fetchBestFriend(profile)
        friendProfileFuture.onComplete {
          case Success(friendProfile) => profile.sendMessage(friendProfile, message)
          case Failure(ex) => ex.printStackTrace()
        }
      case Failure(ex) => ex.printStackTrace()
    }
  }

  // onComplete is a hassle
  // solution: functional composition
  val janeProfileFuture = SocialNetwork.fetchProfile("rtjvm.id.2-jane")
  val janeFuture: Future[String] = janeProfileFuture.map(profile => profile.name) // map transforms value contained inside, ASYNC
  val janesBestFriend: Future[Profile] = janeProfileFuture.flatMap(profile => SocialNetwork.fetchBestFriend(profile))
  val janesBestFriendFilter: Future[Profile] = janesBestFriend.filter(profile => profile.name.startsWith("Z"))

  def sendMessageToBestFriend_v2(accountId: String, message: String): Unit = {
    val profileFuture = SocialNetwork.fetchProfile(accountId)
    val action: Future[Unit] = profileFuture.flatMap {
      profile =>
        SocialNetwork.fetchBestFriend(profile).map {
          bestFriend => profile.sendMessage(bestFriend, message)
        }
    }
  }

  def sendMessageToBestFriend_v3(accountId: String, message: String): Unit = {
    for {
      profile <- SocialNetwork.fetchProfile(accountId)
      bestFriend <- SocialNetwork.fetchBestFriend(profile)
    } yield profile.sendMessage(bestFriend, message)
  }

  // fallbacks
  val profileNoMatterWhat = SocialNetwork.fetchProfile("unknown id").recover {
    case e: Throwable => Profile("rtjvm.id.0-dummy", "Forever Alone")
  }

  val aFetchedProfileNoMatterWhat: Future[Profile] = SocialNetwork.fetchProfile("unknown id").recoverWith {
    case e: Throwable => SocialNetwork.fetchProfile("rtjvm.id.0-dummy")
  }

  val fallbackProfile: Future[Profile] = SocialNetwork.fetchProfile("unknown id").fallbackTo(SocialNetwork.fetchProfile("rtjvm.id.0-dummy"))

  /*
    Block for a future
   */

  case class User(name: String)

  case class Transaction(sender: String, receiver: String, amount: Double, status: String)

  object BankingApp {
    def fetchUser(name: String): Future[User] = Future {
      Thread.sleep(500)
      User(name)
    }

    def createTransaction(user: User, merchantName: String, amount: Double) = Future {
      Thread.sleep(1000)
      Transaction(user.name, merchantName, amount, "SUCCESS")
    }

    // external api
    def purchase(userName: String, item: String, merchantName: String, price: Double): String = {
      /*
        1. fetch user
        2. create transaction
        3. WAIT for transaction to finish
       */
      val transactionStatusFuture: Future[String] = for {
        user <- fetchUser(userName)
        transaction <- createTransaction(user, merchantName, price)
      } yield transaction.status

      Await.result(transactionStatusFuture, 2.seconds) // throws TimeoutException if the future doesn't finish within 2s
    }
  }

  /*
      Promises
   */
  def demoPromises(): Unit = {
    val promise = Promise[Int]()
    val futureInside: Future[Int] = promise.future

    // thread 1 - "consumer": monitor the future for completion
    futureInside.onComplete {
      case Success(value) => println(s"[consumer] I've just been completed $value")
      case Failure(exception) => exception.printStackTrace()
    }

    // thread 2 - "producer"
    val producerThread = new Thread(() => {
      println("Crunching numbers...")
      Thread.sleep(1000)
      // fulfil the promise
      promise.success(42)
      println("[producer] I'm done.")
    })

    producerThread.start()
  }

  /**
   * Exercises
   * 1) Fulfil a future IMMEDIATELY with a value
   * 2) in sequence: make sure the first future has been completed before returning the second
   * 3) first(fa, fb) => new Future with the value of the first future  to complete
   * 4) last(fa, fb) => new Future with the value of the LAST future to complete
   * 5) retry an action returning a Future until a predicate holds true
   */

  //1

  def completeImmediately[A](value: A): Future[A] = Future(value)

  def completeImmediately_v2[A](value: A): Future[A] = Future.successful(value)

  //2
  def inSequence[A, B](first: Future[A], second: Future[B]): Future[B] =
    first.flatMap(_ => second)

  //3
  def first[A](fa: Future[A], fb: Future[A]): Future[A] = {
    val p = Promise[A]()
    fa.onComplete(result1 => p.tryComplete(result1))
    fb.onComplete(result2 => p.tryComplete(result2))

    p.future
  }

  //4
  def last[A](fa: Future[A], fb: Future[A]): Future[A] =
    val bothPromise = Promise[A]()
    val lastPromise = Promise[A]()

    def checkAndComplete(result: Try[A]): Unit = {
      if (!bothPromise.tryComplete(result))
        lastPromise.complete(result)
    }

    fa.onComplete(checkAndComplete)
    fb.onComplete(checkAndComplete)

    lastPromise.future

  //    for {
  //      _ <- fa
  //      if fa.isCompleted
  //    } yield promise.completeWith(fb)
  //
  //    for {
  //      _ <- fb
  //      if fb.isCompleted
  //    } yield promise.completeWith(fa)
  //
  //    fa.flatMap(_ => fb.map(y => promise.trySuccess(y)))
  //    fb.flatMap(_ => fa.map(y => promise.trySuccess(y)))
  //5
  def retryUntil[A](action: () => Future[A], predicate: A => Boolean): Future[A] = {
    action()
      .filter(predicate)
      .recoverWith {
        case _ => retryUntil(action, predicate)
      }
  }
}