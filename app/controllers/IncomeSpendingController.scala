package controllers

import dto.requests.{IncomeSpendingRegisterRequest, IncomeSpendingUpdateRequest}
import entities.{Id, IncomeSpending}
import javax.inject.{Inject, Singleton}
import modules.AuthAction
import play.api.i18n.MessagesApi
import play.api.libs.json.{JsNull, Json}
import play.api.mvc._
import services.IncomeSpendingService

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class IncomeSpendingController @Inject()(
  authAction: AuthAction,
  cc: ControllerComponents,
  messagesApi: MessagesApi,
  service: IncomeSpendingService
)(implicit ec: ExecutionContext)
    extends KakeiboooController(cc, messagesApi) {

  def register(): Action[AnyContent] = authAction.async { implicit request =>
    IncomeSpendingRegisterRequest
      .mappingForm()
      .fold(
        error => Future.successful(BadRequest(convertErrorResponse(error))),
        form =>
          service
            .register(request.userId, form)
            .map(data => ok(Json.toJson(data)))
            .recover {
              case e: Exception =>
                e.printStackTrace() // TODO: Loggerに変える
                badRequest(JsNull)
            }
      )
  }

  def update(): Action[AnyContent] = authAction.async { implicit request =>
    IncomeSpendingUpdateRequest
      .mappingForm()
      .fold(
        error => Future.successful(BadRequest(convertErrorResponse(error))),
        form => {
          service
            .update(request.userId, form)
            .map(data => ok(Json.toJson(data)))
            .recover {
              case e: Exception =>
                e.printStackTrace() // TODO: Loggerに変える
                badRequest(JsNull)
            }
        }
      )
  }

  def list(limitOpt: Option[Int]): Action[AnyContent] = authAction.async { implicit request =>
    service.list(request.userId, limitOpt).map(data => ok(Json.toJson(data)))
  }

  def listOfMonth(yyyyMM: Int, limitOpt: Option[Int]): Action[AnyContent] = authAction.async {
    implicit request =>
      service
        .listOfMonth(request.userId, yyyyMM, limitOpt)
        .map(data => ok(Json.toJson(data)))
  }

  def delete(id: Long): Action[AnyContent] = authAction.async { implicit request =>
    service
      .delete(request.userId, Id[IncomeSpending](id))
      .map(data => ok(Json.toJson(data)))
  }
}
