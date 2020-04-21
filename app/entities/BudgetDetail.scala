package entities

import java.time.ZonedDateTime

case class BudgetDetail(
  budgetDetailId: Id[BudgetDetail],
  budgetId: Id[Budget],
  userId: Id[User],
  amount: Int,
  howToPayId: Option[Int],
  createdAt: Option[ZonedDateTime],
  updatedAt: Option[ZonedDateTime],
  isDeleted: Boolean,
  deletedAt: Option[ZonedDateTime]
)
