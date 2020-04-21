package controllers

import javax.inject.{Inject, Singleton}
import modules.AuthAction
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsNull, Json}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import services.AccountService

import scala.concurrent.ExecutionContext

@Singleton
class AccountController @Inject()(
  authAction: AuthAction,
  cc: ControllerComponents,
  messagesApi: MessagesApi,
  service: AccountService
)(implicit ec: ExecutionContext)
    extends KakeiboooController(cc, messagesApi) {

  def listByUserId(): Action[AnyContent] = authAction.async { implicit request =>
    service
      .list(request.userId)
      .map(data => ok(Json.toJson(data)))
      .recover {
        case e: Exception =>
          e.printStackTrace() // TODO: Loggerに変える
          badRequest(JsNull)
      }
  }
}
