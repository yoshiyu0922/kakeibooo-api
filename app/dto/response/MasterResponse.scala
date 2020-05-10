package dto.response

import caches.HowToPay
import entities.{Category, CategoryDetail}
import play.api.libs.json.{Format, Json}

case class MasterResponse(
  categories: List[CategoryResponse],
  categoryDetails: List[CategoryDetailResponse],
  howToPays: List[HowToPayResponse]
)

case class CategoryResponse(id: Long, name: String, isIncome: Boolean, isDeleted: Boolean)

case class CategoryDetailResponse(id: Long, categoryId: Long, name: String, isDeleted: Boolean)

case class HowToPayResponse(id: Int, name: String)

object MasterResponse {
  implicit val categoryFormat: Format[CategoryResponse] =
    Json.format
  implicit val categoryDetailFormat: Format[CategoryDetailResponse] =
    Json.format
  implicit val howToPayFormat: Format[HowToPayResponse] = Json.format
  implicit val initFormat: Format[MasterResponse] = Json.format

  def fromEntity(
    categories: List[Category],
    categoryDetails: List[CategoryDetail],
    howToPay: List[HowToPay]
  ): MasterResponse = {
    val categoryResponse = categories.map(
      c =>
        CategoryResponse(
          id = c.categoryId.value,
          name = c.name,
          isIncome = c.isIncome,
          isDeleted = c.isDeleted
        )
    )
    val categoryDetailResponse = categoryDetails.map(
      c =>
        CategoryDetailResponse(
          id = c.categoryId.value,
          categoryId = c.categoryId.value,
          name = c.name,
          isDeleted = c.isDeleted
        )
    )
    val howToPayResponse =
      howToPay.map(h => HowToPayResponse(id = h.id, name = h.name))

    MasterResponse(
      categories = categoryResponse,
      categoryDetails = categoryDetailResponse,
      howToPays = howToPayResponse
    )
  }
}
