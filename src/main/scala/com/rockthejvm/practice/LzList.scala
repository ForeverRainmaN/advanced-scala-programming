package com.rockthejvm.practice

import scala.annotation.tailrec

abstract class LzList[A] {
  def isEmpty: Boolean

  def head: A

  def tail: LzList[A]

  infix def #::(element: A): LzList[A] // prepending

  def ++(another: LzList[A]): LzList[A] // TODO warning

  def foreach(f: A => Unit): Unit

  def map[B](f: A => B): LzList[B]

  def flatMap[B](f: A => LzList[B]): LzList[B]

  def filter(predicate: A => Boolean): LzList[A]

  def withFilter(predicate: A => Boolean): LzList[A] = filter(predicate)

  def take(n: Int): LzList[A] // takes the first n elements from this lazy list

  def takeAsList(n: Int): List[A] = take(n).toList

  def toList: List[A] = {
    @tailrec
    def toListAux(remaining: LzList[A], acc: List[A]): List[A] = {
      if (remaining.isEmpty) acc.reverse
      else toListAux(remaining.tail, remaining.head :: acc)
    }

    toListAux(this, List())
  }
}

case class LzEmpty[A]() extends LzList[A] {

  override def isEmpty: Boolean = true

  override def head: A = throw new NoSuchElementException("No head")

  override def tail: LzList[A] = throw new NoSuchElementException("No tail")

  override infix def #::(element: A): LzList[A] = LzCons(element, this)

  override def ++(another: LzList[A]): LzList[A] = another

  override def foreach(f: A => Unit): Unit = ()

  override def map[B](f: A => B): LzList[B] = LzEmpty()

  override def flatMap[B](f: A => LzList[B]): LzList[B] = LzEmpty()

  override def filter(predicate: A => Boolean): LzList[A] = this

  override def take(n: Int): LzList[A] =
    if (n == 0) this
    else throw new RuntimeException(s"Cannot take $n elements from an empty lazy list")
}

class LzCons[A](hd: => A, tl: => LzList[A]) extends LzList[A] {

  override def isEmpty: Boolean = false

  override lazy val head: A = hd

  override lazy val tail: LzList[A] = tl

  override infix def #::(element: A): LzList[A] = LzCons(element, this)

  override infix def ++(another: LzList[A]): LzList[A] = LzCons(head, tail ++ another)

  override def foreach(f: A => Unit): Unit = {
    f(head)
    tail.foreach(f)
  }

  override def map[B](f: A => B): LzList[B] = LzCons(f(head), tail.map(f))

  override def flatMap[B](f: A => LzList[B]): LzList[B] =
    f(head) ++ tail.flatMap(f)

  override def filter(predicate: A => Boolean): LzList[A] = {
    if (predicate(head)) LzCons(head, tail.filter(predicate))
    else tail.filter(predicate) // TODO warning
  }

  override def take(n: Int): LzList[A] =
    if (n <= 0) LzEmpty()
    else if (n == 1) LzCons(head, LzEmpty())
    else LzCons(head, tail.take(n - 1)) // preserves lazy eval
}

object LzList {
  def empty[A]: LzList[A] = LzEmpty()

  def generate[A](start: A)(generator: A => A): LzList[A] =
    LzCons(start, LzList.generate(generator(start))(generator))

  def from[A](list: List[A]): LzList[A] = list.foldLeft(LzList.empty) {
    (currentLzList, newElement) => LzCons(newElement, currentLzList)
  }
}

object LzListPlayground extends App {
  val naturals = LzList.generate(1)(n => n + 1) // infinite list of natural numbers
}