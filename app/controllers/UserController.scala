package controllers

import dto.requests.LoginRequest
import javax.inject.{Inject, Singleton}
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsNull, Json}
import play.api.mvc._
import services.UserService

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserController @Inject()(
  cc: ControllerComponents,
  messagesApi: MessagesApi,
  service: UserService
)(implicit ec: ExecutionContext)
    extends KakeiboooController(cc, messagesApi) {

  def auth(): Action[AnyContent] = Action.async { implicit request =>
    LoginRequest
      .mappingForm()
      .fold(
        error => Future.successful(BadRequest(convertErrorResponse(error))),
        form => {
          service
            .auth(form.userId, form.password)
            .map(token => ok(Json.toJson(token)))
            .recover {
              case _: Exception => badRequest(JsNull)
            }
        }
      )
  }
}
