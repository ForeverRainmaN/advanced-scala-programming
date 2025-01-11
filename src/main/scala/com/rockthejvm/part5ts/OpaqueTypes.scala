package com.rockthejvm.part5ts

object OpaqueTypes {

  object SocialNetwork {
    // some data structures = "domain"
    opaque type Name = String

    object Name {
      def apply(string: String): Name = string
    }

    extension (name: Name)
      def length: Int = name.length // use string api

    // inside, Name <-> String
    def addFriend(person1: Name, person2: Name): Boolean =
      person1.length == person2.length // use the entire string api
  }

  // outside SocialNetwork, Name and String are NOT related

  // val name: Name = "Daniel" // will not compile

  // why: you don't need (or want) to have access to the entire String API for the Name type

  object Graphics {
    opaque type Color = Int // in hex
    opaque type ColorFilter <: Color = Int

    val Red: Color = 0xFF000000
    val Green: Color = 0x00FF0000
    val Blue: Color = 0x0000FF00
    val halfTransparency: ColorFilter = 0x80
  }

  import Graphics.*
  import SocialNetwork.*

  case class OverlayFilter(c: Color)

  val fadeLayer = OverlayFilter(halfTransparency) // ColorFilter <: Color

  // how can we create instances of opaque types + how to access their APIs

  // 1 - companion objects
  val aName = Name("Daniel")

  val nameLength = aName.length

  // 2 - extension methods

  def main(args: Array[String]): Unit = {

  }

}
