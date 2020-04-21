package dto.response.incomeSpending

import play.api.libs.json.{Format, Json}

case class RegisterResponse(
  id: Long
)

object RegisterResponse {
  implicit val format: Format[RegisterResponse] = Json.format
}
