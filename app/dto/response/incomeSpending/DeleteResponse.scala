package dto.response.incomeSpending

import play.api.libs.json.{Format, Json}

case class DeleteResponse(
  id: Long,
  count: Int
)

object DeleteResponse {
  implicit val format: Format[DeleteResponse] = Json.format
}
