package com.rockthejvm.practice

import java.util.Date

object JSONSerialization {
  /*
    Users, posts, feeds
  */

  case class User(name: String, age: Int, email: String)

  case class Post(content: String, createdAt: Date)

  case class Feed(user: User, posts: List[Post])

  /*
    1 - intermediate data: numbers, strings, lists, dates, objects
    2 - type class to convert data to intermediate data
    3 - serialize to JSON
   */

  sealed trait JSONValue {
    def stringify: String
  }

  final case class JSONString(value: String) extends JSONValue {
    override def stringify: String = "\"" + value + "\""
  }

  final case class JSONNumber(value: Int) extends JSONValue {
    override def stringify: String = value.toString
  }

  final case class JSONArray(values: List[JSONValue]) extends JSONValue {
    override def stringify: String = values.map(_.stringify).mkString("[", ",", "]")
  }

  final case class JSONObject(values: Map[String, JSONValue]) extends JSONValue {
    override def stringify: String = values.map {
      case (key, value) => "\"" + key + "\":" + value.stringify
    }.mkString("{", ",", "}")
  }

  // ======== TYPE CLASS PATTERN ========
  // TC Definition
  trait JSONConverter[A] {
    def convert(value: A): JSONValue
  }

  // TC instances for String, Int, Date, User, Post, Feed
  given stringConverter: JSONConverter[String] with
    override def convert(value: String): JSONValue = JSONString(value)

  given intConverter: JSONConverter[Int] with
    override def convert(value: Int): JSONValue = JSONNumber(value)

  given dateConverter: JSONConverter[Date] with
    override def convert(value: Date): JSONValue = JSONString(value.toString)

  given userConverter: JSONConverter[User] with
    override def convert(user: User): JSONValue = JSONObject(Map(
      "name" -> JSONConverter[String].convert(user.name),
      "age" -> JSONConverter[Int].convert(user.age),
      "email" -> JSONConverter[String].convert(user.email)
    ))

  given postConverter: JSONConverter[Post] with
    override def convert(post: Post): JSONValue = JSONObject(Map(
      "content" -> JSONConverter[String].convert(post.content),
      "createdAt" -> JSONConverter[String].convert(post.createdAt.toString)
    ))

  given feedConverter: JSONConverter[Feed] with
    override def convert(feed: Feed): JSONValue = JSONObject(Map(
      "user" -> JSONConverter[User].convert(feed.user),
      "posts" -> JSONArray(feed.posts.map(post => JSONConverter[Post].convert(post)))
    ))

  // User-facing API
  object JSONConverter {
    def convert[A](value: A)(using converter: JSONConverter[A]): JSONValue =
      converter.convert(value)

    def apply[A](using instance: JSONConverter[A]): JSONConverter[A] = instance
  }

  // example
  val now = new Date(System.currentTimeMillis())
  val john = User("John", 34, "jonh@rockthejvm.com")
  val feed = Feed(john, List(
    Post("Hello, I am learning type classes", now),
    Post("Look at this cute puppy!", now),
  ))

  // extension methods
  object JSONSyntax {
    extension [A](value: A)
      def toIntermediate(using converter: JSONConverter[A]): JSONValue =
        converter.convert(value)

      def toJSON(using converter: JSONConverter[A]): String =
        toIntermediate.stringify
  }

  def main(args: Array[String]): Unit = {
    import JSONSyntax.*
    println(feed.toJSON)
  }
}
