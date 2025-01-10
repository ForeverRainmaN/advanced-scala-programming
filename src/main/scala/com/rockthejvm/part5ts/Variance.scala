package com.rockthejvm.part5ts

object Variance {
  class Animal

  class Dog(name: String) extends Animal

  // variance question for List: if dog extends Animal, then should a List[Dog] "extend" List[Animal]
  // for List, YES -> List is COVARIANT

  val lassie = new Dog("Lassie")
  val hachi = new Dog("Hachi")
  val laika = new Dog("Laika")

  val anAnimal: Animal = lassie // this is ok
  val myDogs: List[Animal] = List(lassie, hachi, laika) // ok - list is COVARIANT: a list of dogs is a list of animals

  // define covariant types
  class MyList[+A] // MyList is COVARIANT in A

  val aListOfAnimals: MyList[Animal] = new MyList[Dog]

  // if NO, then the type is INVARIANT
  trait Semigroup[A] { // no marker = INVARIANT
    def combine(x: A, y: A): A
  }

  // java generics
  //val aJavaList: java.util.ArrayList[Animal] = new java.util.ArrayList[Dog] // type mismatch: java generics are all INVARIANT

  // HELL NO - CONTRAVARIANCE
  // if Dog <: Animal, then Vet[Animal] <: Vet[Dog]
  trait Vet[-A] { // contravariant in A
    def heal(animal: A): Boolean
  }

  type Cat >: Animal

  val myVet2: Vet[Dog] = new Vet[Animal] {
    override def heal(animal: Animal): Boolean = true
  }

  val myVet: Vet[Dog] = new Vet[Animal] {
    override def heal(animal: Animal): Boolean = {
      println("hey, you good?")
      true
    }
  }
  // if the vet can treat any animal, she/he can treat my dog too
  val healLaika = myVet.heal(laika) // ok

  class RandomGenerator[+A] // produces values, should be covariant

  class MyOption[+A] // stores values, should be covariant

  class JSONSerializer[-A] // consumes values and turns them into strings

  trait MyFunction[-A, +B]


  // 2 - Add variance modifiers to this "library"
  abstract class LList[+A] {
    def head: A

    def tail: LList[A]
  }

  case object EmptyList extends LList[Nothing] {
    override def head = throw new NoSuchElementException

    override def tail = throw new NoSuchElementException
  }

  case class Cons[+A](override val head: A, override val tail: LList[A]) extends LList[A]

  val animalList = Cons(new Animal, Cons(new Animal, Cons(new Dog("Laika"), EmptyList)))

  /*
    Rule of thumb:
      - if your type PRODUCES or RETRIEVES a value(contains or creates) (e.g. a list), then it should be COVARIANT
      - if your type ACTS ON or CONSUMES a value (e.g. a vet), then it should be CONTRAVARIANT
      - otherwise, INVARIANT
   */
}
