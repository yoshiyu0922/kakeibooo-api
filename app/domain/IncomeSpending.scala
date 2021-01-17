package domain

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
  accountOpt: Option[Account] = None
) {

  lazy val account = accountOpt
    .ensuring(_.isDefined, "account is empty in IncomeSpendService.delete")
    .get

}

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
