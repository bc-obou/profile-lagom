package com.example.profile.impl

import akka.NotUsed
import com.example.profile.api.{Authentications, ClaimsChanges, Identity, Profile, ProfileData, ProfileSearch, ProfileSearchCriteria, ProfileSearchResult, ProfileService, SearchProfile}
import com.lightbend.lagom.scaladsl.api.ServiceCall
import org.slf4j.LoggerFactory

import scala.concurrent.Future

/**
  * Implementation of the HelloWorldService.
  */
class ProfileServiceImpl extends ProfileService {

  private val log = LoggerFactory.getLogger(getClass)

  override def getProfile(id: String): ServiceCall[NotUsed, Profile] = ServiceCall { _ =>
    Future.successful(Profile("1", Vector.empty[Identity], Vector.empty[Authentications]))
  }

  override def createProfile(): ServiceCall[ProfileData, NotUsed] = ServiceCall { profileData =>
    log.info("Create profile data {}", profileData)
    Future.successful(NotUsed)
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
