package usecase

import adapter.dto.response.AuthTokenResponse
import domain.User._
import javax.inject.{Inject, Singleton}
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import pdi.jwt.JwtJson
import play.api.libs.json.Json
import adapter.repositories.UserRepository

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserService @Inject()(val userRepository: UserRepository, val bCrypt: BCryptPasswordEncoder)(
  implicit ec: ExecutionContext
) {

  /**
    * ログイン認証を行う
    *
    * @param frontUserId ユーザーID（表示用）
    * @param password パスワード
    * @return AuthTokenResponse
    */
  def auth(frontUserId: String, password: String): Future[AuthTokenResponse] =
    userRepository
      .auth(frontUserId, password) match {
      case Some(user) if bCrypt.matches(password, user.password) =>
        val json = Json.toJsObject(user)
        val token: String = JwtJson.encode(json)
        Future.successful(AuthTokenResponse(token))
      case None =>
        Future.failed(new RuntimeException("failed to authorization."))
    }
}
