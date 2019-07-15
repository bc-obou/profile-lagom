package com.example.profile.impl

import java.time.ZonedDateTime

import akka.{Done, NotUsed}
import com.example.profile.api.Profile
import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag, PersistentEntity}
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import play.api.libs.json.{Format, JsSuccess, Json, Reads, Writes}

import scala.collection.immutable

class ProfilePersistentEntity extends PersistentEntity {
  override type Command = ProfileCommand[_]
  override type Event = ProfileEvent
  override type State = ProfileState

  override def initialState: ProfileState = ProfileState("",
    Vector.empty[IdentityState], Vector.empty[AuthenticationsState])

  override def behavior: Behavior = Actions().onReadOnlyCommand[GetProfile.type, ProfileState] {
    case (GetProfile, ctx, state) =>
      ctx.reply(state)
  }.onCommand[CreateProfile, ProfileState] {
    case (CreateProfile(profileState), ctx, state) =>
      ctx.thenPersist(ProfileCreated(profileState)) { e =>
        ctx.reply(e.profileState)
      }
  }.onEvent(eventHandlers)

  def eventHandlers: EventHandler = {
    case (ProfileCreated(profileState), state) => profileState
  }
}

sealed trait ProfileCommand[R] extends ReplyType[R]

sealed trait ProfileEvent extends AggregateEvent[ProfileEvent] {
  def aggregateTag: AggregateEventTag[ProfileEvent] = ProfileEvent.Tag
}

object ProfileEvent {
  val Tag: AggregateEventTag[ProfileEvent] = AggregateEventTag[ProfileEvent]
}


case class ProfileState(id: String,
                        identities: Vector[IdentityState],
                        authentications: Vector[AuthenticationsState])

case class IdentityState(id: IdentityIdState, lastUsed: Boolean = false, claims: ClaimsState)

case class IdentityIdState(provider: String, key: String)

case class ClaimsState(firstName: String, lastName: String, email: String, phone: Option[String])

case class AuthenticationsState(identityId: IdentityIdState, logs: Vector[AuthenticationLogState])

case class AuthenticationLogState(time: ZonedDateTime)

case object GetProfile extends ProfileCommand[ProfileState] {
  implicit val format: Format[GetProfile.type] = Format(
    Reads(_ => JsSuccess(GetProfile)),
    Writes(_ => Json.obj())
  )
}

case class CreateProfile(profileState: ProfileState) extends ProfileCommand[ProfileState] {
  implicit val claimsState: Format[ClaimsState] = Json.format[ClaimsState]
  implicit val identityIdState: Format[IdentityIdState] = Json.format[IdentityIdState]
  implicit val identityState: Format[IdentityState] = Json.format[IdentityState]
  implicit val authenticationLogState: Format[AuthenticationLogState] = Json.format[AuthenticationLogState]
  implicit val authenticationsState: Format[AuthenticationsState] = Json.format[AuthenticationsState]
  implicit val profileStateF: Format[ProfileState] = Json.format[ProfileState]
  implicit val format: Format[CreateProfile] = Json.format[CreateProfile]
}

object ProfileSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: immutable.Seq[JsonSerializer[_]] = immutable.Seq(
    JsonSerializer[GetProfile.type],
    JsonSerializer[ProfileCreated]
  )
}

case class ProfileCreated(profileState: ProfileState) extends ProfileEvent

object ProfileCreated {

  /**
    * Format for the update item command.
    *
    * Persistent entities get sharded across the cluster. This means commands
    * may be sent over the network to the node where the entity lives if the
    * entity is not on the same node that the command was issued from. To do
    * that, a JSON format needs to be declared so the command can be serialized
    * and deserialized.
    */

  implicit val claimsState: Format[ClaimsState] = Json.format[ClaimsState]
  implicit val identityIdState: Format[IdentityIdState] = Json.format[IdentityIdState]
  implicit val identityState: Format[IdentityState] = Json.format[IdentityState]
  implicit val authenticationLogState: Format[AuthenticationLogState] = Json.format[AuthenticationLogState]
  implicit val authenticationsState: Format[AuthenticationsState] = Json.format[AuthenticationsState]
  implicit val profileStateF: Format[ProfileState] = Json.format[ProfileState]
  implicit val format: Format[ProfileCreated] = Json.format[ProfileCreated]
}

