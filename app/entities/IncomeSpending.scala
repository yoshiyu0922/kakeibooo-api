package entities

import java.time.{LocalDate, ZonedDateTime}

case class IncomeSpending(
  incomeSpendingId: Id[IncomeSpending],
  userId: Id[User],
  accountId: Id[Account],
  accrualDate: LocalDate,
  categoryDetailId: Id[CategoryDetail],
  amount: Int,
  howToPayId: Option[Int],
  isIncome: Boolean,
  content: String,
  createdAt: ZonedDateTime,
  updatedAt: ZonedDateTime,
  isDeleted: Boolean,
  deletedAt: Option[ZonedDateTime],
  account: Option[Account] = None
)

object IncomeSpending {
  def apply(
    incomeSpendingIdOpt: Option[Id[IncomeSpending]],
    userId: Id[User],
    accountId: Id[Account],
    accrualDate: LocalDate,
    categoryDetailId: Id[CategoryDetail],
    amount: Int,
    howToPayId: Option[Int],
    isIncome: Boolean,
    content: Option[String]
  ): IncomeSpending = IncomeSpending(
    incomeSpendingId = incomeSpendingIdOpt.getOrElse(Id[IncomeSpending]()),
    userId = userId,
    accountId = accountId,
    accrualDate = accrualDate,
    categoryDetailId = categoryDetailId,
    amount = amount,
    howToPayId = howToPayId,
    isIncome = isIncome,
    content = content.getOrElse(""),
    createdAt = ZonedDateTime.now(),
    updatedAt = ZonedDateTime.now(),
    isDeleted = false,
    deletedAt = None
  )
}
