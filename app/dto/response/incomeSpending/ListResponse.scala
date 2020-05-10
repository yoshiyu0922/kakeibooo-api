package dto.response.incomeSpending

import caches.HowToPay
import dto.IncomeSpendingDto
import modules.MasterCache
import play.api.libs.json.{Format, Json}

case class ListResponse(incomeSpendResponses: List[IncomeSpendResponse])

object ListResponse {
  implicit val format: Format[ListResponse] = Json.format

  def fromEntities(entities: List[IncomeSpendingDto], masterCache: MasterCache): ListResponse = {

    val list = entities.map(dto => {
      val entity = dto.incomeSpending
      val category = masterCache.allCategoryDetails
        .find(_.categoryDetailId == entity.categoryDetailId)
        .get
      val parentCategoryName =
        masterCache.allCategories
          .find(_.categoryId == category.categoryId)
          .get
          .name
      val categoryName = masterCache.allCategoryDetails
        .find(_.categoryDetailId == entity.categoryDetailId)
        .get
        .name
      IncomeSpendResponse(
        incomeSpendingId = entity.incomeSpendingId.value,
        userId = entity.userId.value,
        accountId = entity.accountId.value,
        accrualDate = entity.accrualDate,
        parentCategoryId = category.categoryId.value,
        categoryId = entity.categoryDetailId.value,
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
