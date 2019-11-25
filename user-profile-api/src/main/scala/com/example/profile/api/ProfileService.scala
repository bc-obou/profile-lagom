package com.example.profile.api

import java.time.ZonedDateTime

import akka.util.ByteString
import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.deser.MessageSerializer.{NegotiatedDeserializer, NegotiatedSerializer}
import com.lightbend.lagom.scaladsl.api.deser.{MessageSerializer, StrictMessageSerializer}
import com.lightbend.lagom.scaladsl.api.transport.{MessageProtocol, Method, NotAcceptable, UnsupportedMediaType}
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}
import play.api.libs.json.{Format, Json}

import scala.collection.immutable

trait ProfileService extends Service {

  def getProfile(id: String): ServiceCall[NotUsed, Profile]

  def createProfile(): ServiceCall[ProfileData, Profile]

  def changeClaims(id: String, identityId: String, key: String): ServiceCall[ClaimsChanges, NotUsed]

  def searchProfile(): ServiceCall[SearchProfile, ProfileSearch]

  def getSwagger(): ServiceCall[NotUsed, String]

  def getCss(): ServiceCall[NotUsed, String]

  def getJsBundle(): ServiceCall[NotUsed, String]

  def getJsPreset(): ServiceCall[NotUsed, String]

  def getSwaggerJson(): ServiceCall[NotUsed, String]


  override final def descriptor: Descriptor = {
    import Formatters._
    import Service._
    // @formatter:off
    named("user-profile")
      .withCalls(
        restCall(Method.GET, "/v1/profiles/:id", getProfile _),
        restCall(Method.POST, "/v1/profiles", createProfile _),
        restCall(Method.PATCH, "/v1/profiles/:id/identities/:identityId/:key/claims", changeClaims _),
        restCall(Method.POST, "/v1/profiles/search", searchProfile _),
        restCall(Method.GET, "/v1/swagger", getSwagger _)(MessageSerializer.NotUsedMessageSerializer, MsgSer.HtmlMessageSerializer),
        restCall(Method.GET, "/v1/swagger-ui.css", getCss _)(MessageSerializer.NotUsedMessageSerializer, MsgSer.CssMessageSerializer),
        restCall(Method.GET, "/v1/swagger-ui-bundle.js", getJsBundle _)(MessageSerializer.NotUsedMessageSerializer, MsgSer.JsMessageSerializer),
        restCall(Method.GET, "/v1/swagger-ui-standalone-preset.js", getJsPreset _)(MessageSerializer.NotUsedMessageSerializer, MsgSer.JsMessageSerializer),
        restCall(Method.GET, "/v1/swagger/swagger.json", getSwaggerJson _)
      ).withAutoAcl(true)
  }

  object MsgSer {
    implicit val HtmlMessageSerializer: StrictMessageSerializer[String] = new StrictMessageSerializer[String] {
      private val defaultProtocol = MessageProtocol(Some("text/html"), Some("utf-8"), None)
      override val acceptResponseProtocols: immutable.Seq[MessageProtocol] = immutable.Seq(defaultProtocol)

      private class StringSerializer(override val protocol: MessageProtocol) extends NegotiatedSerializer[String, ByteString] {
        override def serialize(s: String) = ByteString.fromString(s, protocol.charset.getOrElse("utf-8"))
      }

      private class StringDeserializer(charset: String) extends NegotiatedDeserializer[String, ByteString] {
        override def deserialize(wire: ByteString) = wire.decodeString(charset)
      }

      override val serializerForRequest: NegotiatedSerializer[String, ByteString] = new StringSerializer(defaultProtocol)

      override def deserializer(protocol: MessageProtocol): NegotiatedDeserializer[String, ByteString] = {
        if (protocol.contentType.forall(_ == "text/html")) {
          new StringDeserializer(protocol.charset.getOrElse("utf-8"))
        } else {
          throw UnsupportedMediaType(protocol, defaultProtocol)
        }
      }

      override def serializerForResponse(acceptedMessageProtocols: immutable.Seq[MessageProtocol]): NegotiatedSerializer[String, ByteString] = {
        if (acceptedMessageProtocols.isEmpty) {
          serializerForRequest
        } else {
          acceptedMessageProtocols.collectFirst {
            case wildcardOrNone if wildcardOrNone.contentType.forall(ct => ct == "*" || ct == "*/*") =>
              new StringSerializer(wildcardOrNone.withContentType("text/html"))
            case textPlain if textPlain.contentType.contains("text/html") =>
              new StringSerializer(textPlain)
          } match {
            case Some(serializer) => serializer
            case None             => throw NotAcceptable(acceptedMessageProtocols, defaultProtocol)
          }
        }
      }
    }
    implicit val CssMessageSerializer: StrictMessageSerializer[String] = new StrictMessageSerializer[String] {
      private val defaultProtocol = MessageProtocol(Some("text/css"), None, None)
      override val acceptResponseProtocols: immutable.Seq[MessageProtocol] = immutable.Seq(defaultProtocol)

      private class StringSerializer(override val protocol: MessageProtocol) extends NegotiatedSerializer[String, ByteString] {
        override def serialize(s: String) = ByteString.fromString(s, protocol.charset.getOrElse("utf-8"))
      }

      private class StringDeserializer(charset: String) extends NegotiatedDeserializer[String, ByteString] {
        override def deserialize(wire: ByteString) = wire.decodeString(charset)
      }

      override val serializerForRequest: NegotiatedSerializer[String, ByteString] = new StringSerializer(defaultProtocol)

      override def deserializer(protocol: MessageProtocol): NegotiatedDeserializer[String, ByteString] = {
        if (protocol.contentType.forall(_ == "text/css")) {
          new StringDeserializer(protocol.charset.getOrElse("utf-8"))
        } else {
          throw UnsupportedMediaType(protocol, defaultProtocol)
        }
      }

      override def serializerForResponse(acceptedMessageProtocols: immutable.Seq[MessageProtocol]): NegotiatedSerializer[String, ByteString] = {
        if (acceptedMessageProtocols.isEmpty) {
          serializerForRequest
        } else {
          acceptedMessageProtocols.collectFirst {
            case wildcardOrNone if wildcardOrNone.contentType.forall(ct => ct == "*" || ct == "*/*") =>
              new StringSerializer(wildcardOrNone.withContentType("text/css"))
            case textPlain if textPlain.contentType.contains("text/css") =>
              new StringSerializer(textPlain)
          } match {
            case Some(serializer) => serializer
            case None             => throw NotAcceptable(acceptedMessageProtocols, defaultProtocol)
          }
        }
      }
    }
    implicit val JsMessageSerializer: StrictMessageSerializer[String] = new StrictMessageSerializer[String] {
      private val defaultProtocol = MessageProtocol(Some("application/javascript"), Some("utf-8"), None)
      override val acceptResponseProtocols: immutable.Seq[MessageProtocol] = immutable.Seq(defaultProtocol)

      private class StringSerializer(override val protocol: MessageProtocol) extends NegotiatedSerializer[String, ByteString] {
        override def serialize(s: String) = ByteString.fromString(s, protocol.charset.getOrElse("utf-8"))
      }

      private class StringDeserializer(charset: String) extends NegotiatedDeserializer[String, ByteString] {
        override def deserialize(wire: ByteString) = wire.decodeString(charset)
      }

      override val serializerForRequest: NegotiatedSerializer[String, ByteString] = new StringSerializer(defaultProtocol)

      override def deserializer(protocol: MessageProtocol): NegotiatedDeserializer[String, ByteString] = {
        if (protocol.contentType.forall(_ == "application/javascript")) {
          new StringDeserializer(protocol.charset.getOrElse("utf-8"))
        } else {
          throw UnsupportedMediaType(protocol, defaultProtocol)
        }
      }

      override def serializerForResponse(acceptedMessageProtocols: immutable.Seq[MessageProtocol]): NegotiatedSerializer[String, ByteString] = {
        if (acceptedMessageProtocols.isEmpty) {
          serializerForRequest
        } else {
          acceptedMessageProtocols.collectFirst {
            case wildcardOrNone if wildcardOrNone.contentType.forall(ct => ct == "*" || ct == "*/*") =>
              new StringSerializer(wildcardOrNone.withContentType("application/javascript"))
            case textPlain if textPlain.contentType.contains("application/javascript") =>
              new StringSerializer(textPlain)
          } match {
            case Some(serializer) => serializer
            case None             => throw NotAcceptable(acceptedMessageProtocols, defaultProtocol)
          }
        }
      }
    }
  }


}

object Formatters {
  /**
    * Format for converting greeting messages to and from JSON.
    *
    * This will be picked up by a Lagom implicit conversion from Play's JSON format to Lagom's message serializer.
    */
  implicit val claims: Format[Claims] = Json.format[Claims]
  implicit val identityId: Format[IdentityId] = Json.format[IdentityId]
  implicit val identity: Format[Identity] = Json.format[Identity]

  implicit val authentications: Format[Authentications] = Json.format[Authentications]
  implicit val authenticationLog: Format[AuthenticationLog] = Json.format[AuthenticationLog]
  implicit val profile: Format[Profile] = Json.format[Profile]

  implicit val profileData: Format[ProfileData] = Json.format[ProfileData]
  implicit val claimsChanges: Format[ClaimsChanges] = Json.format[ClaimsChanges]

  implicit val profileSearchCriteria: Format[ProfileSearchCriteria] = Json.format[ProfileSearchCriteria]
  implicit val profileSearchShape: Format[ProfileSearchShape] = Json.format[ProfileSearchShape]
  implicit val profileSearchResult: Format[ProfileSearchResult] = Json.format[ProfileSearchResult]
  implicit val profileSearch: Format[ProfileSearch] = Json.format[ProfileSearch]
  implicit val searchProfileFormatter: Format[SearchProfile] = Json.format[SearchProfile]
}

case class Profile(id: String,
                   identities: Vector[Identity],
                   authentications: Vector[Authentications])

case class Identity(id: IdentityId, lastUsed: Boolean = false, claims: Claims)

case class IdentityId(provider: String, key: String)

case class Claims(firstName: String, lastName: String, email: String, phone: Option[String])

case class Authentications(identityId: IdentityId, logs: Vector[AuthenticationLog])

case class AuthenticationLog(time: ZonedDateTime)

case class ProfileData(identityId: IdentityId, claims: Claims, log: AuthenticationLog)

case class ClaimsChanges(firstName: Option[String], lastName: Option[String], email: Option[String], phone: Option[String])

case class SearchProfile(criteria: ProfileSearchCriteria, shape: Option[ProfileSearchShape])

case class ProfileSearchCriteria(id: Option[String],
                                 identityId: Option[IdentityId])

case class ProfileSearchShape(identities: Option[Boolean],
                              authentications: Option[Boolean])

case class ProfileSearchResult(id: String,
                               identities: Option[Vector[Identity]] = None,
                               authentications: Option[Vector[Authentications]] = None)

case class ProfileSearch(criteria: ProfileSearchCriteria,
                         shape: Option[ProfileSearchShape] = None,
                         result: Vector[ProfileSearchResult])
