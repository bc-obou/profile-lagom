package com.example.profile.impl

import java.util.UUID

import akka.{Done, NotUsed}
import com.example.profile.api.{Authentications, Claims, ClaimsChanges, Identity, IdentityId, Profile, ProfileData, ProfileSearch, ProfileSearchCriteria, ProfileSearchResult, ProfileService, SearchProfile}
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.{PersistentEntityRef, PersistentEntityRegistry}
import org.slf4j.LoggerFactory
import io.bfil.automapper._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

/**
  * Implementation of the HelloWorldService.
  */
class ProfileServiceImpl(persistentEntityRegistry: PersistentEntityRegistry)(implicit ec: ExecutionContext) extends ProfileService {

  private val log = LoggerFactory.getLogger(getClass)

  private def entityRef(id: String): PersistentEntityRef[ProfileCommand[_]] =
    persistentEntityRegistry.refFor[ProfilePersistentEntity](id)

  override def getProfile(id: String): ServiceCall[NotUsed, Profile] = ServiceCall { _ =>
    log.info("#" * 50 + "get profile {}", id)
    val aaa = entityRef(id).ask(GetProfile).map(profile => convertProfile(id, profile))
    aaa.onComplete {
      case Success(value) => log.info("1" * 50 + "get profile {}", value)
      case Failure(exception) => log.info("2" * 50 + "get profile {}", exception)
    }
    aaa
  }

  private def convertProfile(id: String, profileState: ProfileState): Profile = {
    Profile(
      id = profileState.id,
      identities = identityConverter(profileState),
      //      identities = Vector.empty[Identity],
      authentications = Vector.empty[Authentications] //todo convert
    )
  }

  private def identityConverter(p: ProfileState): Vector[Identity] = {
    p.identities.map(identity => Identity(id = IdentityId(identity.id.provider, identity.id.provider),
      claims = Claims(
        firstName = identity.claims.firstName,
        lastName = identity.claims.lastName,
        email = identity.claims.email,
        phone = identity.claims.phone
      )))
  }

  override def createProfile(): ServiceCall[ProfileData, Profile] = ServiceCall { profileData =>
    val id = UUID.randomUUID().toString
    val profileState = convertToProfileState(id, profileData)
    log.info("profile state {}", profileState)
    entityRef(id).ask(CreateProfile(profileState)).map(p => convertProfile(p.id, p))
  }

  private def convertToProfileState(id: String, profileData: ProfileData): ProfileState = {
    //    ProfileState(
    //      id = profileState.id,
    //      identities = identityConverter(profileState),
    //      authentications = Vector.empty[Authentications] //todo convert
    //    )
    ProfileState(
      id = id,
      identities = Vector(IdentityState(id = IdentityIdState(key = profileData.identityId.key, provider = profileData.identityId.provider),
        claims = automap(profileData.claims).to[ClaimsState])),
      authentications = Vector.empty[AuthenticationsState] //todo convert
    )
  }

  private def identityConverter2(p: ProfileState): Vector[Identity] = {
    p.identities.map(identity => Identity(id = IdentityId(identity.id.provider, identity.id.provider),
      claims = Claims(
        firstName = identity.claims.firstName,
        lastName = identity.claims.lastName,
        email = identity.claims.email,
        phone = identity.claims.phone
      )))
  }

  override def changeClaims(id: String, identityId: String, key: String): ServiceCall[ClaimsChanges, NotUsed] =
    ServiceCall { changeClaims =>
      log.info("Change claims data {}", changeClaims)
      Future.successful(NotUsed)
    }

  override def searchProfile(): ServiceCall[SearchProfile, ProfileSearch] = ServiceCall { search =>
    log.info("Search profile data {}", search)
    Future.successful(ProfileSearch(
      ProfileSearchCriteria(id = Some("1"), identityId = None), None, Vector.empty[ProfileSearchResult]))
  }

}
