package dto.response

import play.api.libs.json.{Format, JsValue, Json}

case class ErrorResponse(
  id: String,
  message: String
)

object ErrorResponse {
  implicit val format: Format[ErrorResponse] = Json.format

  implicit class ImplicitErrorResponses(self: Seq[ErrorResponse]) {
    def toResponseJson: JsValue = Json.toJson(self)
  }
}
