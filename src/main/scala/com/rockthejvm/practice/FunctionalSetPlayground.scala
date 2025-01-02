package com.rockthejvm.practice

import scala.annotation.{tailrec, targetName}

abstract class FSet[A] extends (A => Boolean) {
  def contains(elem: A): Boolean

  def apply(elem: A): Boolean = contains(elem)

  @targetName("add")
  infix def +(elem: A): FSet[A]

  @targetName("concat")
  infix def ++(anotherSet: FSet[A]): FSet[A]

  def map[B](f: A => B): FSet[B]

  def flatMap[B](f: A => FSet[B]): FSet[B]

  def filter(predicate: A => Boolean): FSet[A]

  def foreach(f: A => Unit): Unit

  @targetName("remove")
  infix def -(elem: A): FSet[A]

  @targetName("difference")
  infix def --(anotherSet: FSet[A]): FSet[A]

  @targetName("intersection")
  infix def &(anotherSet: FSet[A]): FSet[A]

  @targetName("negation")
  def unary_! : FSet[A] = new PBSet(x => !contains(x))
}

// { x in N | x % 2 == 0 }
// property-based set
class PBSet[A](property: A => Boolean) extends FSet[A] {

  override def contains(elem: A): Boolean = property(elem)

  @targetName("add")
  override infix def +(elem: A): FSet[A] =
    new PBSet(x => x == elem || property(x))

  @targetName("concat")
  override infix def ++(anotherSet: FSet[A]): FSet[A] =
    new PBSet(x => anotherSet(x) || property(x))

  override def map[B](f: A => B): FSet[B] = politelyFail()

  override def flatMap[B](f: A => FSet[B]): FSet[B] = politelyFail()

  override def filter(predicate: A => Boolean): FSet[A] = new PBSet(x => predicate(x) && property(x))

  override def foreach(f: A => Unit): Unit = politelyFail()

  @targetName("remove")
  override infix def -(elem: A): FSet[A] = filter(x => x != elem)

  @targetName("difference")
  override infix def --(anotherSet: FSet[A]): FSet[A] = filter(!anotherSet)

  @targetName("intersection")
  override infix def &(anotherSet: FSet[A]): FSet[A] = filter(anotherSet)

  private def politelyFail() = throw new RuntimeException("I dont know if this set is iterable")
}

case class AllInclusiveSet[A]() extends PBSet[A](_ => true)

case class Empty[A]() extends FSet[A] {

  override def contains(elem: A): Boolean = false

  @targetName("add")
  override infix def +(elem: A): FSet[A] = Cons(elem, this)

  @targetName("concat")
  override infix def ++(anotherSet: FSet[A]): FSet[A] = anotherSet

  override def map[B](f: A => B): FSet[B] = Empty()

  override def flatMap[B](f: A => FSet[B]): FSet[B] = Empty()

  override def filter(predicate: A => Boolean): FSet[A] = this

  override def foreach(f: A => Unit): Unit = ()

  @targetName("remove")
  override infix def -(elem: A): FSet[A] = this

  @targetName("difference")
  override infix def --(anotherSet: FSet[A]): FSet[A] = this

  @targetName("intersection")
  override infix def &(anotherSet: FSet[A]): FSet[A] = this
}

case class Cons[A](head: A, tail: FSet[A]) extends FSet[A] {

  override def contains(elem: A): Boolean = elem == head || tail.contains(elem)

  @targetName("add")
  override infix def +(elem: A): FSet[A] =
    if (contains(elem)) this
    else Cons(elem, this)

  @targetName("concat")
  override infix def ++(anotherSet: FSet[A]): FSet[A] = tail ++ anotherSet + head

  override def map[B](f: A => B): FSet[B] = tail.map(f) + f(head)

  override def flatMap[B](f: A => FSet[B]): FSet[B] =
    tail.flatMap(f) ++ f(head)

  override def filter(predicate: A => Boolean): FSet[A] =
    val filteredTail = tail.filter(predicate)
    if predicate(head) then filteredTail + head else filteredTail

  override def foreach(f: A => Unit): Unit =
    f(head)
    tail.foreach(f)

  @targetName("remove")
  override infix def -(elem: A): FSet[A] =
    if head == elem then tail else tail - elem + head

  @targetName("difference")
  override infix def --(anotherSet: FSet[A]): FSet[A] = filter(x => !anotherSet(x))

  @targetName("intersection")
  override infix def &(anotherSet: FSet[A]): FSet[A] = filter(anotherSet)
}

object FSet {
  def apply[A](values: A*): FSet[A] = {
    @tailrec
    def buildSet(valuesSeq: Seq[A], acc: FSet[A]): FSet[A] =
      if (valuesSeq.isEmpty) acc
      else buildSet(valuesSeq.tail, acc + valuesSeq.head)

    buildSet(values, Empty())
  }
}

object FunctionalSetPlayground extends App {

  val naturals = new PBSet[Int](_ => true)
  println(naturals.contains(38493849))
  println(!naturals.contains(0))
  println((!naturals + 1 + 2 + 3).contains(3))
}
