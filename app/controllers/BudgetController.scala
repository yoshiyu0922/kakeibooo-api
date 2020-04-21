package controllers

import dto.requests.BudgetBundleRequest
import javax.inject.{Inject, Singleton}
import modules.AuthAction
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsNull, Json}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import services.BudgetService

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BudgetController @Inject()(
  authAction: AuthAction,
  cc: ControllerComponents,
  messagesApi: MessagesApi,
  service: BudgetService
)(implicit ec: ExecutionContext)
    extends KakeiboooController(cc, messagesApi) {

  def list(yyyyMM: Int): Action[AnyContent] = authAction.async { implicit request =>
    service.list(request.userId, yyyyMM).map(res => ok(Json.toJson(res)))
  }

  def registerOrUpdate(): Action[AnyContent] =
    authAction.async(
      implicit request =>
        BudgetBundleRequest
          .mappingForm()
          .fold(
            error => Future.successful(BadRequest(convertErrorResponse(error))),
            form => {
              service
                .registerOrUpdate(request.userId, form)
                .map(data => ok(Json.toJson(data)))
                .recover {
                  case e: Exception =>
                    e.printStackTrace() // TODO: Loggerに変える
                    badRequest(JsNull)
                }
            }
          )
    )
}
