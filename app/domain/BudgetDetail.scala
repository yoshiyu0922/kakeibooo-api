package domain

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

object BudgetDetail {
  def apply(
    budget: Budget,
    amount: Int,
    howToPayId: Int
  ): BudgetDetail = BudgetDetail(
    budgetDetailId = Id[BudgetDetail](),
    budgetId = budget.budgetId,
    userId = budget.userId,
    amount = amount,
    howToPayId = Option(howToPayId),
    createdAt = None,
    updatedAt = None,
    isDeleted = false,
    deletedAt = None
  )
}
