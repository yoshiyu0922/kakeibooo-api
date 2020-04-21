package dto.response.budget

import entities.Budget
import play.api.libs.json.{Format, Json}

case class CreateOrUpdateResponse(
  id: Long
)

object CreateOrUpdateResponse {
  implicit val format: Format[CreateOrUpdateResponse] = Json.format

  def fromEntity(entity: Budget): CreateOrUpdateResponse =
    CreateOrUpdateResponse(entity.budgetId.value)
}
