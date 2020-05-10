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
  deletedAt: Option[ZonedDateTime]
)
