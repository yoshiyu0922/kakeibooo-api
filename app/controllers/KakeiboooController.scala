package controllers

import dto.response.ErrorResponse
import javax.inject.{Inject, Singleton}
import play.api.data.Form
import play.api.http.Writeable
import play.api.i18n.{Lang, MessagesApi}
import play.api.libs.json.JsValue
import play.api.mvc.{AbstractController, ControllerComponents, Result}

@Singleton
class KakeiboooController @Inject()(
  cc: ControllerComponents,
  messagesApi: MessagesApi
) extends AbstractController(cc) {
  private val lang: Lang = Lang("ja")

  def convertErrorResponse[A](form: Form[A]): JsValue =
    form.errors
      .map(
        e =>
          ErrorResponse(
            id = e.key,
            message = messagesApi.apply(e.message)(lang)
          )
      )
      .toResponseJson

  def ok[A](s: A)(implicit writeAble: Writeable[A]): Result =
    Ok(s).withHeaders(
      "Access-Control-Allow-Origin" -> "*",
      "Accept" -> "application/json",
      "Access-Control-Allow-Methods" -> "POST, GET, PUT, OPTIONS, PATCH, DELETE",
      "Access-Control-Allow-Headers" -> "Origin, Authorization, Accept, Content-Type",
      "Content-Type" -> "application/json"
    )

  def badRequest[A](s: A)(implicit writeAble: Writeable[A]): Result =
    BadRequest(s).withHeaders(
      "Access-Control-Allow-Origin" -> "*",
      "Accept" -> "application/json",
      "Access-Control-Allow-Methods" -> "POST, GET, PUT, OPTIONS, PATCH, DELETE",
      "Access-Control-Allow-Headers" -> "Origin, Authorization, Accept, Content-Type",
      "Content-Type" -> "application/json"
    )
}
