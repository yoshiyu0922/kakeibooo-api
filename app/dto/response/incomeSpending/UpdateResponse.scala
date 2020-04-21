package dto.response.incomeSpending

import play.api.libs.json.{Format, Json}

case class UpdateResponse(
  id: Long
)

object UpdateResponse {
  implicit val format: Format[UpdateResponse] = Json.format
}
