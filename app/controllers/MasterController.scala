package controllers

import caches.HowToPay
import dto.response.MasterResponse
import javax.inject.{Inject, Singleton}
import modules.MasterCache
import play.api.i18n.MessagesApi
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MasterController @Inject()(
  cc: ControllerComponents,
  messageApi: MessagesApi,
  masterCache: MasterCache
)(implicit ec: ExecutionContext)
    extends KakeiboooController(cc, messageApi) {

  def all(): Action[AnyContent] = Action.async { implicit request =>
    Future {
      val parentCategories = masterCache.allCategories
      val categories = masterCache.allCategoryDetails
      val howToPays = HowToPay.list
      val response =
        MasterResponse.fromEntity(parentCategories, categories, howToPays)
      ok(Json.toJson(response))
    }
  }

}
