package dto.response

import play.api.libs.json.{Format, Json}

case class AuthToken(
  token: String
)

object AuthToken {
  implicit val format: Format[AuthToken] = Json.format
}
