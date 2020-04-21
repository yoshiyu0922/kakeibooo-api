package dto.response

import caches.HowToPay
import entities.{Category, ParentCategory}
import play.api.libs.json.{Format, Json}

case class MasterResponse(
  parentCategories: List[ParentCategoryResponse],
  categories: List[CategoryResponse],
  howToPays: List[HowToPayResponse]
)

case class ParentCategoryResponse(
  id: Long,
  name: String,
  isIncome: Boolean,
  isDeleted: Boolean
)

case class CategoryResponse(
  id: Long,
  parentCategoryId: Long,
  name: String,
  isDeleted: Boolean
)

case class HowToPayResponse(
  id: Int,
  name: String
)

object MasterResponse {
  implicit val parentCategoryFormat: Format[ParentCategoryResponse] = Json.format
  implicit val categoryFormat: Format[CategoryResponse] = Json.format
  implicit val howToPayFormat: Format[HowToPayResponse] = Json.format
  implicit val initFormat: Format[MasterResponse] = Json.format

  def fromEntity(
    parentCategories: List[ParentCategory],
    categories: List[Category],
    howToPay: List[HowToPay]
  ): MasterResponse = {
    val parentCategoryResponse = parentCategories.map(
      c =>
        ParentCategoryResponse(
          id = c.parentCategoryId.value,
          name = c.name,
          isIncome = c.isIncome,
          isDeleted = c.isDeleted
        )
    )
    val categoryResponse = categories.map(
      c =>
        CategoryResponse(
          id = c.categoryId.value,
          parentCategoryId = c.parentCategoryId.value,
          name = c.name,
          isDeleted = c.isDeleted
        )
    )
    val howToPayResponse = howToPay.map(
      h =>
        HowToPayResponse(
          id = h.id,
          name = h.name
        )
    )

    MasterResponse(
      parentCategories = parentCategoryResponse,
      categories = categoryResponse,
      howToPays = howToPayResponse
    )
  }
}
