package dto.response.incomeSpending

import caches.HowToPay
import dto.IncomeSpendingDto
import modules.MasterCache
import play.api.libs.json.{Format, Json}

case class ListResponse(
  incomeSpendResponses: List[IncomeSpendResponse]
)

object ListResponse {
  implicit val format: Format[ListResponse] = Json.format

  def fromEntities(
    entities: List[IncomeSpendingDto],
    masterCache: MasterCache
  ): ListResponse = {

    val list = entities.map(dto => {
      val entity = dto.incomeSpending
      val category = masterCache.allCategories.find(_.categoryId == entity.categoryId).get
      val parentCategoryName =
        masterCache.allParentCategories
          .find(_.parentCategoryId == category.parentCategoryId)
          .get
          .name
      val categoryName = masterCache.allCategories.find(_.categoryId == entity.categoryId).get.name
      IncomeSpendResponse(
        incomeSpendingId = entity.incomeSpendingId.value,
        userId = entity.userId.value,
        accountId = entity.accountId.value,
        accrualDate = entity.accrualDate,
        parentCategoryId = category.parentCategoryId.value,
        categoryId = entity.categoryId.value,
        accountName = dto.account.name,
        parentCategoryName = parentCategoryName,
        categoryName = categoryName,
        amount = entity.amount,
        howToPayId = entity.howToPayId,
        howToPayName = HowToPay.nameById(entity.howToPayId),
        isIncome = entity.isIncome,
        content = entity.content,
        isDeleted = entity.isDeleted,
        createdAt = entity.createdAt.toLocalDateTime
      )
    })
    ListResponse(list)
  }
}
