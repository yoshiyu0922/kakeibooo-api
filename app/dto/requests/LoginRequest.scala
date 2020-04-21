package dto.requests

import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

case class LoginRequest(
  userId: String,
  password: String
)

object LoginRequest {
  def mappingForm[T]()(implicit request: Request[T]): Form[LoginRequest] =
    Form(
      mapping(
        "userId" -> nonEmptyText,
        "password" -> nonEmptyText
      )(LoginRequest.apply)(LoginRequest.unapply)
    ).bindFromRequest()
}
