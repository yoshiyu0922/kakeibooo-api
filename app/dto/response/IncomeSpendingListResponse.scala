package dto.response

import java.time.{LocalDate, ZonedDateTime}

import codemaster.HowToPay
import entities._
import modules.MasterCache

case class IncomeSpendingListResponse(
  id: Id[IncomeSpending],
  userId: Id[User],
  accountId: Id[Account],
  accrualDate: LocalDate,
  categoryId: Id[Category],
  categoryDetailId: Id[CategoryDetail],
  accountName: String,
  categoryName: String,
  categoryDetailName: String,
  amount: Int,
  howToPayId: Option[Int],
  howToPayName: String,
  isIncome: Boolean,
  content: String,
  createdAt: ZonedDateTime,
  isDeleted: Boolean
)

object IncomeSpendingListResponse {

  def apply(entity: IncomeSpending, masterCache: MasterCache): IncomeSpendingListResponse = {
    val categoryDetail =
      masterCache.allCategoryDetails
        .find(_.categoryDetailId == entity.categoryDetailId)
        .ensuring(_.nonEmpty, "not found category detail")
        .get

    val category = masterCache.allCategories
      .find(_.categoryId == categoryDetail.categoryId)
      .ensuring(_.nonEmpty, "not found category")
      .get

    IncomeSpendingListResponse(
      id = entity.incomeSpendingId,
      userId = entity.userId,
      accountId = entity.accountId,
      accrualDate = entity.accrualDate,
      categoryId = category.categoryId,
      categoryDetailId = categoryDetail.categoryDetailId,
      accountName = entity.account.name,
      categoryName = category.name,
      categoryDetailName = categoryDetail.name,
      amount = entity.amount,
      howToPayId = entity.howToPayId,
      howToPayName = HowToPay.nameById(entity.howToPayId),
      isIncome = entity.isIncome,
      content = entity.content,
      createdAt = entity.createdAt,
      isDeleted = entity.isDeleted
    )
  }
}
