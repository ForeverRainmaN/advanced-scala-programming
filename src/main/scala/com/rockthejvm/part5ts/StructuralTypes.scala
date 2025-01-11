package com.rockthejvm.part5ts

import scala.language.reflectiveCalls

object StructuralTypes {

  type SoundMaker = { // structural type
    def makeSound(): Unit
  }

  class Dog {
    def makeSound(): Unit = println("bark")
  }

  class Car {
    def makeSound(): Unit = println("vrooom")
  }

  val dog: SoundMaker = new Dog // ok
  val car: SoundMaker = new Car // duck typing

  // type refinements
  abstract class Animal {
    def eat(): String
  }

  type WalkingAnimal = Animal { // refined type
    def walk(): Int
  }

  // why: creating type-safe APIs for existing types following the same structure, but no connection to each other
  type JavaClosable = java.io.Closeable

  class CustomClosable {
    def close(): Unit = println("ok ok I am closing")

    def closeSilently(): Unit = println("not making a sound, I promise")
  }

  //  def closeResource(closable: JavaClosable | CustomClosable): Unit =
  //    closable.close() // not ok

  // solution: structural type
  type UnifiedCloseable = {
    def close(): Unit
  }

  def closeResource(closeable: UnifiedCloseable): Unit = closeable.close()

  val jCloseable = new JavaClosable {
    override def close(): Unit = println("closing java resource")
  }

  val cCloseable = new CustomClosable

  def closeResource_v2(closeable: {def close(): Unit}): Unit = closeable.close()

  def main(args: Array[String]): Unit = {
    dog.makeSound() // through reflection (SLOW)
    car.makeSound()

    closeResource(jCloseable)
    closeResource(cCloseable)
  }
}
