package com.example.profile.api

import java.time.ZonedDateTime

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}
import play.api.libs.json.{Format, Json}

trait ProfileService extends Service {

  def getProfile(id: String): ServiceCall[NotUsed, Profile]

  def createProfile(): ServiceCall[ProfileData, Profile]

  def changeClaims(id: String, identityId: String, key: String): ServiceCall[ClaimsChanges, NotUsed]

  def searchProfile(): ServiceCall[SearchProfile, ProfileSearch]


  override final def descriptor: Descriptor = {
    import Formatters._
    import Service._
    // @formatter:off
    named("user-profile")
      .withCalls(
        restCall(Method.GET, "/v1/profiles/:id", getProfile _),
        restCall(Method.POST, "/v1/profiles", createProfile _),
        restCall(Method.PATCH, "/v1/profiles/:id/identities/:identityId/:key/claims", changeClaims _),
        restCall(Method.POST, "/v1/profiles/search", searchProfile _)
      ).withAutoAcl(true)
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
