package services

import dto.response.AuthToken
import entities.User._
import javax.inject.{Inject, Singleton}
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import pdi.jwt.JwtJson
import play.api.libs.json.Json
import repositories.UserRepository

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserService @Inject()(val userRepository: UserRepository, val bCrypt: BCryptPasswordEncoder)(
  implicit ec: ExecutionContext
) {

  def auth(userId: String, password: String): Future[AuthToken] =
    userRepository
      .find(userId, password) match {
      case Some(user) if bCrypt.matches(password, user.password) =>
        val json = Json.toJsObject(user)
        val token: String = JwtJson.encode(json)
        Future.successful(AuthToken(token))
      case None =>
        Future.failed(new RuntimeException("failed to authorization."))
    }
}
