import java.time.{Clock, Instant, LocalDateTime, ZoneOffset, ZonedDateTime}
import java.util.Date

import akka.http.scaladsl.model.DateTime
import cats.data.{EitherT, OptionT}

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
//import cats.data.EitherT
// import cats.data.EitherT

import cats.implicits._
import scala.concurrent.duration._
//import cats.syntax._

case class User(name: String, id: String)
case class Account(name: String, id: String)
case class Class3(name: String, id: String)

object Main {

  def main(args: Array[String]): Unit = {

    println(DateTime.now.clicks)
    println(ZonedDateTime.now(ZoneOffset.UTC).getOffset.getTotalSeconds)
//      println(ZonedDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli)
    println( Instant.now().atOffset(ZoneOffset.UTC).toInstant.toEpochMilli)
    println( Clock.systemUTC().instant().toEpochMilli)

    import java.time.ZoneOffset
    import java.time.ZonedDateTime
    val utc = Instant.ofEpochMilli(1576139994000L).atZone(ZoneOffset.ofHours(3))
    println(utc)

  }

//    val account = getUseIdByName("name").flatMap {
//      case Left(v) => Future.successful(Left(v))
//      case Right(r) => getUserById(r).flatMap {
//        case Left(v) => Future.successful(Left(v))
//        case Right(u) => getAccountByUser(u).map {
//          case Left(v) => Left(v)
//          case Right(r) => Right(r)
//        }
//      }
//    }
//
//    for {
//     id <- getUseIdByName("name")
//    } id
//
//   val aaa = EitherT(getUseIdByName("name")).flatMap(aaa => EitherT(getUserById(aaa))).flatMap(aaa => EitherT(getAccountByUser(aaa)))
//
//    val userId = EitherT(getUseIdByName("name"))
//   val aaa2 =  for {
//      id <- EitherT(getUseIdByName("name"))
//      user <- EitherT(getUserById(id))
//      account <- EitherT(getAccountByUser(user))
//    } yield account
//
//
//    val result = aaa2.value
//
//    Await.result(result, 1 second)
//    println(result)
//
//    val customGreeting: Future[Option[String]] = Future.successful(Some("welcome back, Lola"))
//
//    val value = OptionT(customGreeting).value
//
//    OptionT(customGreeting)
//    Await.result(value, 1 second)
//    println(value)
//
//    val greetingFO: Future[Option[String]] = Future.successful(Some("Hello"))
//
//    val firstnameF: Future[User] = Future.successful(User("name", "id"))
//
//    val lastnameO: Option[String] = Some("Doe")
//
//    val ot: OptionT[Future, String] = for {
////      g <- OptionT(greetingFO)
//      f <- OptionT.liftF(firstnameF)
////      l <- OptionT.fromOption[Future](lastnameO)
//    } yield s"$g $f $l"
//
//    val result2: Future[Option[String]] = ot.value // Future(Some("Hello Jane Doe"))
//  }
//
//  def getUseIdByName(name: String): Future[Either[String, String]] = Future.successful(Left("Cannot find Id"))
////  def getUseIdByName(name: String): Future[Either[String, String]] = Future.successful(Right("123"))
//  def getUserById(id: String): Future[Either[String, User]] = Future.successful(Left("Cannot find user"))
////  def getUserById(id: String): Future[Either[String, User]] = Future.successful(Right(User("name", "id")))
//  def getAccountByUser(user: User): Future[Either[String, Account]] = Future.successful(Right(Account("account", "account-id")))

}
