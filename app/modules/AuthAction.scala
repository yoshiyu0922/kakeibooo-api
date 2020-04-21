package modules

import entities.{Id, User}
import javax.inject.Inject
import pdi.jwt.JwtJson
import play.api.libs.json.JsObject
import play.api.mvc._
import repositories.UserRepository
import utils.TryUtil._

import scala.concurrent.{ExecutionContext, Future}

case class UserRequest[A](userId: Id[User], request: Request[A]) extends WrappedRequest[A](request)

class AuthAction @Inject()(val parser: BodyParsers.Default, val repository: UserRepository)(
  implicit val executionContext: ExecutionContext
) extends ActionBuilder[UserRequest, AnyContent] {

  override def invokeBlock[A](
    request: Request[A],
    block: UserRequest[A] => Future[Result]
  ): Future[Result] =
    request.headers.get("Authorization") match {
      case Some(token) =>
        val value = token.replaceAll("Bearer ", "")
        JwtJson
          .decodeJson(value)
          .toFuture
          .flatMap(json => authorize(json, request))
          .flatMap(block)
      case None =>
        Future.failed(new RuntimeException("Authorization failed"))
    }

  private def authorize[A](json: JsObject, request: Request[A]): Future[UserRequest[A]] = {
    val userId =
      json.value
        .get("userId")
        .ensuring(_.isDefined, "userId is empty")
        .get("value")

    repository.resolveById(Id[User](userId.toString().toLong)) match {
      case Some(user) => Future.successful(UserRequest(user.userId, request))
      case None       => Future.failed(new RuntimeException("authorize failed"))
    }
  }
}
