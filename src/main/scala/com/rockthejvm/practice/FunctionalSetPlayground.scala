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
}

case class Empty[A]() extends FSet[A] {

  override def contains(elem: A): Boolean = false

  @targetName("add")
  override infix def +(elem: A): FSet[A] =
    if (contains(elem)) this
    else Cons(elem, this)

  @targetName("concat")
  override infix def ++(anotherSet: FSet[A]): FSet[A] = anotherSet

  override def map[B](f: A => B): FSet[B] = Empty()

  override def flatMap[B](f: A => FSet[B]): FSet[B] = Empty()

  override def filter(predicate: A => Boolean): FSet[A] = this

  override def foreach(f: A => Unit): Unit = ()
}

case class Cons[A](head: A, tail: FSet[A]) extends FSet[A] {

  override def contains(elem: A): Boolean = elem == head || tail.contains(elem)

  @targetName("add")
  override infix def +(elem: A): FSet[A] = Cons(elem, this)

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
}
